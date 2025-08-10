package com.gymmanagement.gym_app.controller.inventory;

import com.gymmanagement.gym_app.domain.inventory.Proveedor;
import com.gymmanagement.gym_app.repository.inventory.ProveedorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventario/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorRepository proveedorRepository;

    @GetMapping
    public ResponseEntity<List<Proveedor>> listarProveedores() {
        return ResponseEntity.ok(proveedorRepository.findByActivoTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> obtenerProveedor(@PathVariable Long id) {
        return proveedorRepository.findById(id)
                .filter(Proveedor::isActivo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Proveedor>> buscarPorRazonSocial(
            @RequestParam String razonSocial) {
        return ResponseEntity.ok(
                proveedorRepository.findByRazonSocialContainingIgnoreCase(razonSocial)
        );
    }

    @PostMapping
    public ResponseEntity<Proveedor> crearProveedor(@Valid @RequestBody Proveedor proveedor) {
        // Validar que el RUC no esté registrado
        if (proveedorRepository.existsByRuc(proveedor.getRuc())) {
            throw new IllegalStateException("Ya existe un proveedor con el RUC: " + proveedor.getRuc());
        }
        return new ResponseEntity<>(proveedorRepository.save(proveedor), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizarProveedor(
            @PathVariable Long id,
            @Valid @RequestBody Proveedor proveedorActualizado) {
        return proveedorRepository.findById(id)
                .filter(Proveedor::isActivo)
                .map(proveedor -> {
                    // Validar que el RUC no esté en uso por otro proveedor
                    if (!proveedor.getRuc().equals(proveedorActualizado.getRuc()) &&
                            proveedorRepository.existsByRuc(proveedorActualizado.getRuc())) {
                        throw new IllegalStateException("El RUC ya está en uso por otro proveedor");
                    }
                    
                    proveedor.setRuc(proveedorActualizado.getRuc());
                    proveedor.setRazonSocial(proveedorActualizado.getRazonSocial());
                    proveedor.setContacto(proveedorActualizado.getContacto());
                    proveedor.setTelefono(proveedorActualizado.getTelefono());
                    proveedor.setEmail(proveedorActualizado.getEmail());
                    proveedor.setDireccion(proveedorActualizado.getDireccion());
                    
                    return ResponseEntity.ok(proveedorRepository.save(proveedor));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long id) {
        return proveedorRepository.findById(id)
                .map(proveedor -> {
                    // Verificar si el proveedor tiene productos asociados
                    // if (productoRepository.existsByProveedor(proveedor)) {
                    //     throw new IllegalStateException("No se puede eliminar el proveedor porque tiene productos asociados");
                    // }
                    
                    proveedor.setActivo(false);
                    proveedorRepository.save(proveedor);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
