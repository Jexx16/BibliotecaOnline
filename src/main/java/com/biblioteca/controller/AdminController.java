package com.biblioteca.controller;

import com.biblioteca.model.Usuario;
import com.biblioteca.model.Libro;
import com.biblioteca.model.SolicitudPrestamo;
import com.biblioteca.repository.UsuarioRepository;
import com.biblioteca.repository.LibroRepository;
import com.biblioteca.repository.SolicitudPrestamoRepository;  // ← NUEVO IMPORT
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin
public class AdminController {

    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;
    private final SolicitudPrestamoRepository solicitudRepository;  // ← NUEVO

    public AdminController(
            UsuarioRepository usuarioRepository,
            LibroRepository libroRepository,
            SolicitudPrestamoRepository solicitudRepository) {  // ← AGREGADO
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
        this.solicitudRepository = solicitudRepository;  // ← INICIALIZADO
    }

    // ============================================
    // GESTIÓN DE USUARIOS (ya existente)
    // ============================================
    @GetMapping("/usuarios")
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @PostMapping("/usuarios")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        try {
            List<Usuario> existentes = usuarioRepository.findAll();
            for (Usuario u : existentes) {
                if (u.getNombre().equalsIgnoreCase(usuario.getNombre())) {
                    return ResponseEntity.status(400).body("El nombre de usuario ya existe");
                }
            }
            
            if (usuario.getPassword() == null || usuario.getPassword().length() < 6) {
                return ResponseEntity.status(400).body("La contraseña debe tener al menos 6 caracteres");
            }
            
            usuario.setId(null);
            Usuario nuevo = usuarioRepository.save(usuario);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear usuario: " + e.getMessage());
        }
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario existente = usuarioRepository.findById(id).orElse(null);
            if (existente == null) {
                return ResponseEntity.notFound().build();
            }
            
            existente.setNombre(usuario.getNombre());
            existente.setRol(usuario.getRol());
            existente.setTipo(usuario.getTipo());
            
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                existente.setPassword(usuario.getPassword());
            }
            
            usuarioRepository.save(existente);
            return ResponseEntity.ok(existente);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar: " + e.getMessage());
        }
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        try {
            usuarioRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar");
        }
    }

    // ============================================
    // GESTIÓN DE LIBROS (ya existente)
    // ============================================
    @GetMapping("/libros")
    public List<Libro> listarLibros() {
        return libroRepository.findAll();
    }

    @PostMapping("/libros")
    public ResponseEntity<?> crearLibro(@RequestBody Libro libro) {
        try {
            if (libro.getTitulo() == null || libro.getTitulo().trim().isEmpty()) {
                return ResponseEntity.status(400).body("El título es obligatorio");
            }
            
            if (libro.getCantidad() == null || libro.getCantidad() < 1) {
                libro.setCantidad(1);
            }
            libro.setCantidadDisponible(libro.getCantidad());
            libro.setDisponible(libro.getCantidad() > 0);
            
            libro.setId(null);
            Libro nuevo = libroRepository.save(libro);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear libro: " + e.getMessage());
        }
    }

    @PutMapping("/libros/{id}")
    public ResponseEntity<?> actualizarLibro(@PathVariable Long id, @RequestBody Libro libro) {
        try {
            Libro existente = libroRepository.findById(id).orElse(null);
            if (existente == null) {
                return ResponseEntity.notFound().build();
            }
            
            existente.setTitulo(libro.getTitulo());
            existente.setAutor(libro.getAutor());
            existente.setEditorial(libro.getEditorial());
            
            int diferencia = libro.getCantidad() - existente.getCantidad();
            existente.setCantidad(libro.getCantidad());
            existente.setCantidadDisponible(existente.getCantidadDisponible() + diferencia);
            if (existente.getCantidadDisponible() < 0) {
                existente.setCantidadDisponible(0);
            }
            existente.setDisponible(existente.getCantidadDisponible() > 0);
            
            libroRepository.save(existente);
            return ResponseEntity.ok(existente);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar: " + e.getMessage());
        }
    }

    @DeleteMapping("/libros/{id}")
    public ResponseEntity<?> eliminarLibro(@PathVariable Long id) {
        try {
            libroRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar: " + e.getMessage());
        }
    }

    // ============================================
    // GESTIÓN DE SOLICITUDES DE PRÉSTAMO (NUEVO)
    // ============================================

    @GetMapping("/solicitudes")
    public List<SolicitudPrestamo> listarSolicitudes() {
        return solicitudRepository.findAll();
    }

    @GetMapping("/solicitudes/pendientes")
    public List<SolicitudPrestamo> solicitudesPendientes() {
        return solicitudRepository.findByEstado("PENDIENTE");
    }

    @PostMapping("/solicitudes/{id}/aprobar")
    public ResponseEntity<?> aprobarSolicitud(@PathVariable Long id) {
        try {
            SolicitudPrestamo solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
            
            // Verificar que el libro existe y tiene disponibilidad
            Libro libro = libroRepository.findById(solicitud.getLibroId())
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
            
            if (libro.getCantidadDisponible() <= 0) {
                return ResponseEntity.status(400).body("No hay copias disponibles de este libro");
            }
            
            // Cambiar estado
            solicitud.setEstado("APROBADO");
            solicitud.setFechaAprobacion(LocalDate.now());
            
            // Reducir cantidad disponible del libro
            libro.setCantidadDisponible(libro.getCantidadDisponible() - 1);
            libro.setDisponible(libro.getCantidadDisponible() > 0);
            
            libroRepository.save(libro);
            solicitudRepository.save(solicitud);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al aprobar: " + e.getMessage());
        }
    }

    @PostMapping("/solicitudes/{id}/rechazar")
    public ResponseEntity<?> rechazarSolicitud(@PathVariable Long id) {
        try {
            SolicitudPrestamo solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
            
            solicitud.setEstado("RECHAZADO");
            solicitudRepository.save(solicitud);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al rechazar: " + e.getMessage());
        }
    }

    @PostMapping("/solicitudes/{id}/devolver")
    public ResponseEntity<?> devolverLibro(@PathVariable Long id) {
        try {
            SolicitudPrestamo solicitud = solicitudRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
            
            if (!"APROBADO".equals(solicitud.getEstado())) {
                return ResponseEntity.status(400).body("Esta solicitud no está aprobada");
            }
            
            Libro libro = libroRepository.findById(solicitud.getLibroId())
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
            
            solicitud.setEstado("DEVUELTO");
            solicitud.setFechaDevolucion(LocalDate.now());
            
            // Devolver copia al inventario
            libro.setCantidadDisponible(libro.getCantidadDisponible() + 1);
            libro.setDisponible(true);
            
            libroRepository.save(libro);
            solicitudRepository.save(solicitud);
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al devolver: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> estadisticas() {
        try {
            long totalUsuarios = usuarioRepository.count();
            long totalLibros = libroRepository.count();
            long solicitudesPendientes = solicitudRepository.findByEstado("PENDIENTE").size();
            
            return ResponseEntity.ok(new EstadisticasDTO(totalUsuarios, totalLibros, solicitudesPendientes));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener estadísticas");
        }
    }

    // Clase interna para estadísticas
    static class EstadisticasDTO {
        public long usuarios;
        public long libros;
        public long pendientes;
        
        public EstadisticasDTO(long usuarios, long libros, long pendientes) {
            this.usuarios = usuarios;
            this.libros = libros;
            this.pendientes = pendientes;
        }
    }
}