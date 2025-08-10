package com.gymmanagement.gym_app.controller.inventory;

import com.gymmanagement.gym_app.domain.inventory.Venta;
import com.gymmanagement.gym_app.dto.inventory.VentaDTO;
import com.gymmanagement.gym_app.dto.inventory.requests.VentaRequest;
import com.gymmanagement.gym_app.service.inventory.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @PostMapping
    public ResponseEntity<Venta> crearVenta(@Valid @RequestBody VentaRequest ventaRequest) {
        return new ResponseEntity<>(
                ventaService.crearVenta(ventaRequest),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<Venta> anularVenta(
            @PathVariable Long id,
            @RequestParam String motivo) {
        return ResponseEntity.ok(ventaService.anularVenta(id, motivo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> obtenerVenta(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.convertToDTO(ventaService.findById(id)));
    }

    @GetMapping
    public ResponseEntity<Page<Venta>> listarVentas(Pageable pageable) {
        return ResponseEntity.ok(ventaService.findAll(pageable));
    }

    @GetMapping("/rango-fechas")
    public ResponseEntity<List<Venta>> obtenerVentasPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(ventaService.findByFechas(fechaInicio, fechaFin));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Venta>> obtenerVentasPorCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(ventaService.findByClienteId(clienteId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Venta>> obtenerVentasPorUsuario(@PathVariable UUID usuarioId) {
        return ResponseEntity.ok(ventaService.findByUsuarioId(usuarioId));
    }
}
