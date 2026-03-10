package com.biblioteca.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.biblioteca.repository.LibroRepository;
import com.biblioteca.model.Libro;

@RestController
@RequestMapping("/libros")
@CrossOrigin

public class LibroController{

private final LibroRepository repo;

public LibroController(LibroRepository repo){

this.repo=repo;

}

@GetMapping
public List<Libro> obtenerLibros(){

return repo.findAll();

}

@GetMapping("/buscar/{titulo}")
public List<Libro> buscarLibro(@PathVariable String titulo){

return repo.findByTituloContaining(titulo);

}

}