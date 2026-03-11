package com.biblioteca.repository;

import com.biblioteca.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
    // Buscar reservas por usuario
    List<Reserva> findByUsuarioId(Long usuarioId);
    
    // Contar cuántas reservas tiene un usuario
    int countByUsuarioId(Long usuarioId);
    
    // Verificar si un usuario ya reservó un libro específico
    boolean existsByUsuarioIdAndLibroId(Long usuarioId, Long libroId);
}