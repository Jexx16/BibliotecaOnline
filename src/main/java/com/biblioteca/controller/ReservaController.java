package com.biblioteca.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

import com.biblioteca.model.Reserva;
import com.biblioteca.repository.ReservaRepository;
import com.biblioteca.service.ReservaService;

@RestController
@RequestMapping("/reservas")
@CrossOrigin

public class ReservaController{

private final ReservaService service;
private final ReservaRepository repo;

public ReservaController(ReservaService service,ReservaRepository repo){

this.service=service;
this.repo=repo;

}

@PostMapping

public Reserva reservar(@RequestBody Reserva reserva){

return service.crearReserva(reserva);

}

@GetMapping("/usuario/{id}")

public List<Reserva> historial(@PathVariable Long id){

return repo.findByUsuarioId(id);

}

}