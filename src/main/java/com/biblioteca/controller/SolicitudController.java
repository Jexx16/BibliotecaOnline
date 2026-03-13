package com.biblioteca.controller;

import com.biblioteca.model.SolicitudPrestamo;
import com.biblioteca.model.Libro;
import com.biblioteca.repository.SolicitudPrestamoRepository;
import com.biblioteca.repository.LibroRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.Map;
import java.util.List;  // ← IMPORTACIÓN AGREGADA

@RestController
@RequestMapping("/solicitudes")
@CrossOrigin
public class SolicitudController {

    private final SolicitudPrestamoRepository solicitudRepository;
    private final LibroRepository libroRepository;

    public SolicitudController(
            SolicitudPrestamoRepository solicitudRepository,
            LibroRepository libroRepository) {
        this.solicitudRepository = solicitudRepository;
        this.libroRepository = libroRepository;
    }

    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody Map<String, Long> payload) {
        try {
            Long usuarioId = payload.get("usuarioId");
            Long libroId = payload.get("libroId");

            // Validar que el libro existe
            Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));

            // Validar disponibilidad
            if (libro.getCantidadDisponible() <= 0) {
                return ResponseEntity.status(400).body("No hay copias disponibles de este libro");
            }

            // Validar que no tenga una solicitud pendiente del mismo libro
            boolean yaSolicitado = solicitudRepository
                .existsByUsuarioIdAndLibroIdAndEstado(usuarioId, libroId, "PENDIENTE");
            
            if (yaSolicitado) {
                return ResponseEntity.status(400).body("Ya tienes una solicitud pendiente para este libro");
            }

            // Crear la solicitud
            SolicitudPrestamo solicitud = new SolicitudPrestamo();
            solicitud.setUsuarioId(usuarioId);
            solicitud.setLibroId(libroId);
            solicitud.setFechaSolicitud(LocalDate.now());
            solicitud.setEstado("PENDIENTE");

            SolicitudPrestamo nueva = solicitudRepository.save(solicitud);
            return ResponseEntity.ok(nueva);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear solicitud: " + e.getMessage());
        }
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public List<SolicitudPrestamo> solicitudesPorUsuario(@PathVariable Long usuarioId) {
        return solicitudRepository.findByUsuarioId(usuarioId);
    }
}