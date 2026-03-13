package com.biblioteca.controller;

import com.biblioteca.model.Reserva;
import com.biblioteca.service.ReservaService;
import com.biblioteca.repository.ReservaRepository;  // ← IMPORTANTE: importar el repositorio
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservas")
@CrossOrigin
public class ReservaController {
    
    private final ReservaService reservaService;
    private final ReservaRepository reservaRepository;  // ← DECLARAR EL REPOSITORIO

    // Constructor con AMBOS parámetros
    public ReservaController(ReservaService reservaService, ReservaRepository reservaRepository) {
        this.reservaService = reservaService;
        this.reservaRepository = reservaRepository;  // ← INICIALIZAR
    }

    @PostMapping
    public ResponseEntity<?> crearReserva(@RequestBody Map<String, Long> payload) {
        try {
            Long usuarioId = payload.get("usuarioId");
            Long libroId = payload.get("libroId");
            
            Reserva reserva = reservaService.crearReserva(usuarioId, libroId);
            return ResponseEntity.ok(reserva);
            
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor");
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> historial(@PathVariable Long usuarioId) {
        try {
            List<Reserva> reservas = reservaService.obtenerHistorial(usuarioId);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al obtener historial");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        try {
            reservaService.cancelarReserva(id);
            return ResponseEntity.ok("Reserva cancelada con éxito");
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al cancelar reserva");
        }
    }

    @GetMapping("/todas")
    public List<Reserva> todasLasReservas() {
        return reservaRepository.findAll();  // ← AHORA SÍ FUNCIONA
    }
}