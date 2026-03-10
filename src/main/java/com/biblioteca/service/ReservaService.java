package com.biblioteca.service;

import org.springframework.stereotype.Service;
import java.util.List;
import com.biblioteca.repository.ReservaRepository;
import com.biblioteca.model.Reserva;

@Service
public class ReservaService{

private final ReservaRepository repo;

public ReservaService(ReservaRepository repo){

this.repo=repo;

}

public Reserva crearReserva(Reserva reserva){

List<Reserva> reservasUsuario=repo.findByUsuarioId(reserva.getUsuarioId());

if(reservasUsuario.size()>=5){

throw new RuntimeException("No puede reservar más de 5 libros");

}

return repo.save(reserva);

}

}