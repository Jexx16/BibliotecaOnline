package com.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.biblioteca.model.Reserva;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long>{
    List<Reserva> findByUsuarioId(Long usuarioId);
}