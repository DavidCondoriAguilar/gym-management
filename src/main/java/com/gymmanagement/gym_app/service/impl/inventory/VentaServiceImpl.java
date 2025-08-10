package com.gymmanagement.gym_app.service.impl.inventory;

import com.gymmanagement.gym_app.domain.Usuario;
import com.gymmanagement.gym_app.domain.enums.EstadoVenta;
import com.gymmanagement.gym_app.domain.inventory.*;
import com.gymmanagement.gym_app.dto.inventory.DetalleVentaDTO;
import com.gymmanagement.gym_app.dto.inventory.VentaDTO;
import com.gymmanagement.gym_app.dto.inventory.requests.DetalleVentaRequest;
import com.gymmanagement.gym_app.dto.inventory.requests.VentaRequest;
import com.gymmanagement.gym_app.exception.InsufficientStockException;
import com.gymmanagement.gym_app.exception.ResourceNotFoundException;
import com.gymmanagement.gym_app.repository.UserRepository;
import com.gymmanagement.gym_app.repository.inventory.ProductoRepository;
import com.gymmanagement.gym_app.repository.inventory.VentaRepository;
import com.gymmanagement.gym_app.service.inventory.InventarioService;
import com.gymmanagement.gym_app.service.inventory.VentaService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final UserRepository usuarioRepository;
    private final InventarioService inventarioService;
    private final ModelMapper modelMapper;

    @PostConstruct
    public void configureModelMapper() {
        // Clear existing mappings to avoid conflicts
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        
        // Configure Venta to VentaDTO mapping
        modelMapper.typeMap(Venta.class, VentaDTO.class).addMappings(mapper -> {
            // Map basic fields
            mapper.map(Venta::getId, VentaDTO::setId);
            mapper.map(Venta::getCodigo, VentaDTO::setCodigo);
            mapper.map(Venta::getFechaVenta, VentaDTO::setFechaVenta);
            mapper.map(Venta::getSubtotal, VentaDTO::setSubtotal);
            mapper.map(Venta::getIgv, VentaDTO::setIgv);
            mapper.map(Venta::getTotal, VentaDTO::setTotal);
            mapper.map(Venta::getTipoPago, VentaDTO::setTipoPago);
            mapper.map(Venta::getEstado, VentaDTO::setEstado);
            mapper.map(Venta::getObservaciones, VentaDTO::setObservaciones);
            
            // Map relationships with proper type handling
            // Convert UUID to Long using least significant bits
            mapper.using(ctx -> {
                Venta src = (Venta) ctx.getSource();
                return src.getCliente() != null ? 
                    src.getCliente().getId().getLeastSignificantBits() & Long.MAX_VALUE : 
                    null;
            }).map(Venta::getCliente, VentaDTO::setClienteId);
                
            mapper.using(ctx -> {
                Venta src = (Venta) ctx.getSource();
                return src.getUsuario() != null ? 
                    src.getUsuario().getId().getLeastSignificantBits() & Long.MAX_VALUE : 
                    null;
            }).map(Venta::getUsuario, VentaDTO::setUsuarioId);
                
            mapper.using(ctx -> {
                Venta src = (Venta) ctx.getSource();
                return src.getUsuario() != null ? src.getUsuario().getUsername() : null;
            }).map(Venta::getUsuario, VentaDTO::setUsuarioNombre);
            
            // Map detalles collection using the convertDetalleToDTO method
            mapper.using(ctx -> {
                List<DetalleVenta> detalles = (List<DetalleVenta>) ctx.getSource();
                return detalles.stream()
                    .map(this::convertDetalleToDTO)
                    .collect(Collectors.toList());
            }).map(Venta::getDetalles, VentaDTO::setDetalles);
        });

        // Configure VentaDTO to Venta mapping
        modelMapper.typeMap(VentaDTO.class, Venta.class).addMappings(mapper -> {
            mapper.skip(Venta::setId);
            mapper.skip(Venta::setDetalles);
            mapper.skip(Venta::setCliente);
            mapper.skip(Venta::setUsuario);
        });
        
        // Configure DetalleVenta to DetalleVentaDTO mapping
        modelMapper.typeMap(DetalleVenta.class, DetalleVentaDTO.class).addMappings(mapper -> {
            mapper.map(DetalleVenta::getId, DetalleVentaDTO::setId);
            mapper.map(src -> src.getProducto().getId(), DetalleVentaDTO::setProductoId);
            mapper.map(src -> src.getProducto().getNombre(), DetalleVentaDTO::setProductoNombre);
            mapper.map(src -> src.getProducto().getCodigo(), DetalleVentaDTO::setProductoCodigo);
            mapper.map(DetalleVenta::getCantidad, DetalleVentaDTO::setCantidad);
            mapper.map(DetalleVenta::getPrecioUnitario, DetalleVentaDTO::setPrecioUnitario);
            mapper.map(DetalleVenta::getDescuento, DetalleVentaDTO::setDescuento);
            mapper.map(DetalleVenta::getSubtotal, DetalleVentaDTO::setSubtotal);
        });
    }

    private DetalleVentaDTO convertToDetalleVentaDTO(DetalleVenta detalle) {
        if (detalle == null) {
            return null;
        }
        DetalleVentaDTO dto = new DetalleVentaDTO();
        dto.setId(detalle.getId());
        dto.setProductoId(detalle.getProducto().getId());
        dto.setProductoNombre(detalle.getProducto().getNombre());
        dto.setProductoCodigo(detalle.getProducto().getCodigo());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setDescuento(detalle.getDescuento());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }

    @Override
    @Transactional
    public Venta crearVenta(VentaRequest ventaRequest) {
        // Validar que el usuario existe
        Usuario usuario = usuarioRepository.findById(ventaRequest.getUsuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + ventaRequest.getUsuarioId()));
        
        // Crear la venta
        Venta venta = new Venta();
        venta.setCodigo(generarCodigoVenta());
        venta.setUsuario(usuario);
        venta.setTipoPago(ventaRequest.getTipoPago());
        venta.setObservaciones(ventaRequest.getObservaciones());
        venta.setEstado(EstadoVenta.PENDIENTE);
        
        // Si hay un cliente asociado
        if (ventaRequest.getClienteId() != null) {
            Usuario cliente = usuarioRepository.findById(ventaRequest.getClienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + ventaRequest.getClienteId()));
            venta.setCliente(cliente);
        }
        
        // Procesar los detalles de la venta
        for (DetalleVentaRequest detalleRequest : ventaRequest.getDetalles()) {
            Producto producto = productoRepository.findById(detalleRequest.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + detalleRequest.getProductoId()));
            
            // Validar stock
            if (producto.getStock() < detalleRequest.getCantidad()) {
                throw new InsufficientStockException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            
            // Crear detalle de venta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleRequest.getCantidad());
            detalle.setPrecioUnitario(detalleRequest.getPrecioUnitario());
            detalle.setDescuento(detalleRequest.getDescuento());
            
            venta.agregarDetalle(detalle);
            
            // Convertir UUID a Long para el inventario (usando los bits menos significativos)
            long usuarioIdAsLong = ventaRequest.getUsuarioId().getLeastSignificantBits() & Long.MAX_VALUE;
            
            // Registrar salida de inventario
            inventarioService.registrarSalida(
                    producto,
                    detalleRequest.getCantidad(),
                    "Venta #" + venta.getCodigo(),
                    usuarioIdAsLong,
                    venta.getId() // Usar el ID de la venta
            );
        }
        
        // Calcular totales
        venta.calcularTotales();
        
        // Guardar la venta
        Venta ventaGuardada = ventaRepository.save(venta);
        
        // Actualizar referencias en los movimientos de inventario
        ventaGuardada.getDetalles().forEach(detalle -> {
            // Aquí podrías actualizar la referencia a la venta en los movimientos de inventario
        });
        
        return ventaGuardada;
    }

    @Override
    @Transactional
    public Venta anularVenta(Long ventaId, String motivo) {
        Venta venta = ventaRepository.findById(ventaId)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + ventaId));
        
        if (venta.getEstado() == EstadoVenta.CANCELADA) {
            throw new IllegalStateException("La venta ya está cancelada");
        }
        
        // Revertir el inventario
        for (DetalleVenta detalle : venta.getDetalles()) {
            // Convertir UUID a Long para el inventario (usando los bits menos significativos)
            long usuarioIdAsLong = venta.getUsuario().getId().getLeastSignificantBits() & Long.MAX_VALUE;
            
            inventarioService.registrarEntrada(
                    detalle.getProducto(),
                    detalle.getCantidad(),
                    "Devolución por anulación de venta #" + venta.getCodigo() + ". Motivo: " + motivo,
                    usuarioIdAsLong
            );
        }
        
        // Actualizar estado de la venta
        venta.setEstado(EstadoVenta.CANCELADA);
        venta.setObservaciones("Anulada: " + motivo);
        
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public Venta findById(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return ventaRepository.findByFechas(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Venta> findAll(Pageable pageable) {
        return ventaRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByClienteId(UUID clienteId) {
        if (clienteId == null) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo");
        }
        return ventaRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findByUsuarioId(UUID usuarioId) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo");
        }
        return ventaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public VentaDTO convertToDTO(Venta venta) {
        if (venta == null) {
            return null;
        }
        VentaDTO dto = modelMapper.map(venta, VentaDTO.class);
        
        // Mapear detalles
        if (venta.getDetalles() != null) {
            List<DetalleVentaDTO> detallesDTO = venta.getDetalles().stream()
                    .map(this::convertDetalleToDTO)
                    .collect(Collectors.toList());
            dto.setDetalles(detallesDTO);
        }
        
        // Mapear información de cliente si existe
        if (venta.getCliente() != null) {
            // Convertir UUID a Long usando los bits menos significativos
            long clienteId = venta.getCliente().getId().getLeastSignificantBits() & Long.MAX_VALUE;
            dto.setClienteId(clienteId);
            dto.setClienteNombre(venta.getCliente().getUsername());
        }
        
        // Mapear información del vendedor
        if (venta.getUsuario() != null) {
            // Convertir UUID a Long usando los bits menos significativos
            long usuarioId = venta.getUsuario().getId().getLeastSignificantBits() & Long.MAX_VALUE;
            dto.setUsuarioId(usuarioId);
            dto.setUsuarioNombre(venta.getUsuario().getUsername());
        }
        
        return dto;
    }

    @Override
    public Venta convertToEntity(VentaDTO ventaDTO) {
        return modelMapper.map(ventaDTO, Venta.class);
    }
    
    private DetalleVentaDTO convertDetalleToDTO(DetalleVenta detalle) {
        DetalleVentaDTO dto = modelMapper.map(detalle, DetalleVentaDTO.class);
        if (detalle.getProducto() != null) {
            dto.setProductoId(detalle.getProducto().getId());
            dto.setProductoNombre(detalle.getProducto().getNombre());
            dto.setProductoCodigo(detalle.getProducto().getCodigo());
        }
        return dto;
    }
    
    private String generarCodigoVenta() {
        // Implementar lógica para generar un código de venta único
        return "V-" + System.currentTimeMillis();
    }
}
