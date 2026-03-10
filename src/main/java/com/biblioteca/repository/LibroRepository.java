package com.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.biblioteca.model.Libro;
import java.util.List;

public interface LibroRepository extends JpaRepository<Libro,Long>{

List<Libro> findByTituloContaining(String titulo);

}