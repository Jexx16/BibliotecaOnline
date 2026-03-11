package com.biblioteca.controller;

import com.biblioteca.model.Libro;
import com.biblioteca.repository.LibroRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/libros")
@CrossOrigin
public class LibroController {
    
    private final LibroRepository libroRepository;
    
    public LibroController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }
    
    @GetMapping("/buscar/{titulo}")
    public List<Libro> buscarPorTitulo(@PathVariable String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo);
    }
    @GetMapping("/{id}")
public Libro obtenerLibro(@PathVariable Long id) {
    return libroRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Libro no encontrado"));
}
}