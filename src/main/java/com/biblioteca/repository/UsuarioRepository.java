package com.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.biblioteca.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,Long>{

Usuario findByNombreAndPassword(String nombre,String password);

}