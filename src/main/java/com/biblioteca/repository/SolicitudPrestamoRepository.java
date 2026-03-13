package com.biblioteca.repository;

import com.biblioteca.model.SolicitudPrestamo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SolicitudPrestamoRepository extends JpaRepository<SolicitudPrestamo, Long> {
    
    // Buscar por estado
    List<SolicitudPrestamo> findByEstado(String estado);
    
    // Buscar por usuario
    List<SolicitudPrestamo> findByUsuarioId(Long usuarioId);
    
    // Buscar por libro
    List<SolicitudPrestamo> findByLibroId(Long libroId);
    
    // Verificar si ya existe una solicitud pendiente para ese usuario y libro
    boolean existsByUsuarioIdAndLibroIdAndEstado(Long usuarioId, Long libroId, String estado);
}