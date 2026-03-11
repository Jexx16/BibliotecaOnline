package com.biblioteca.service;

import java.util.List;
import com.biblioteca.model.Reserva;
import com.biblioteca.model.Libro;
import com.biblioteca.repository.ReservaRepository;
import com.biblioteca.repository.LibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Service
public class ReservaService {
    
    private final ReservaRepository reservaRepository;
    private final LibroRepository libroRepository;
    
    public ReservaService(ReservaRepository reservaRepository, LibroRepository libroRepository) {
        this.reservaRepository = reservaRepository;
        this.libroRepository = libroRepository;
    }
    
    @Transactional
    public Reserva crearReserva(Long usuarioId, Long libroId) {
        System.out.println("\n=== NUEVA RESERVA ===");
        System.out.println("Usuario ID: " + usuarioId);
        System.out.println("Libro ID: " + libroId);
        
        try {
            // ===========================================
            // VALIDACIÓN 1: Verificar que el libro existe
            // ===========================================
            Libro libro = libroRepository.findById(libroId)
                .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
            
            System.out.println("Libro: " + libro.getTitulo());
            System.out.println("Copias disponibles: " + libro.getCantidadDisponible());
            
            // ===========================================
            // VALIDACIÓN 2: Verificar disponibilidad del libro
            // ===========================================
            if (libro.getCantidadDisponible() <= 0) {
                throw new RuntimeException("No hay copias disponibles de este libro");
            }
            
            // ===========================================
            // VALIDACIÓN 3: Contar reservas actuales del usuario
            // ===========================================
            int reservasActivas = reservaRepository.countByUsuarioId(usuarioId);
            System.out.println("Reservas activas del usuario: " + reservasActivas);
            
            // ⚠️ REGLA CORRECTA: Puede tener HASTA 5 reservas
            if (reservasActivas >= 5) {
                throw new RuntimeException("Límite de 5 reservas alcanzado. No puedes reservar más.");
            }
            
            // ===========================================
            // VALIDACIÓN 4: Verificar que NO reserve el MISMO libro dos veces
            // ===========================================
            boolean yaReservado = reservaRepository.existsByUsuarioIdAndLibroId(usuarioId, libroId);
            
            if (yaReservado) {
                throw new RuntimeException("Ya has reservado este libro anteriormente.");
            }
            
            // ===========================================
            // CREAR LA RESERVA
            // ===========================================
            Reserva reserva = new Reserva();
            reserva.setUsuarioId(usuarioId);
            reserva.setLibroId(libroId);
            reserva.setFechaReserva(LocalDate.now());
            
            // ===========================================
            // ACTUALIZAR INVENTARIO DEL LIBRO
            // ===========================================
            int nuevaCantidad = libro.getCantidadDisponible() - 1;
            libro.setCantidadDisponible(nuevaCantidad);
            libroRepository.save(libro);
            
            // ===========================================
            // GUARDAR LA RESERVA
            // ===========================================
            Reserva reservaGuardada = reservaRepository.save(reserva);
            
            System.out.println("✅ RESERVA EXITOSA!");
            System.out.println("=== FIN RESERVA ===\n");
            
            return reservaGuardada;
            
        } catch (RuntimeException e) {
            System.out.println("❌ ERROR: " + e.getMessage());
            throw e;
        }
    }
    
    @Transactional
    public void cancelarReserva(Long reservaId) {
        System.out.println("\n=== CANCELAR RESERVA ===");
        
        Reserva reserva = reservaRepository.findById(reservaId)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        
        Libro libro = libroRepository.findById(reserva.getLibroId())
            .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
        
        int nuevaCantidad = libro.getCantidadDisponible() + 1;
        libro.setCantidadDisponible(nuevaCantidad);
        libroRepository.save(libro);
        
        reservaRepository.deleteById(reservaId);
        
        System.out.println("✅ RESERVA CANCELADA");
    }
    
    public List<Reserva> obtenerHistorial(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }
}