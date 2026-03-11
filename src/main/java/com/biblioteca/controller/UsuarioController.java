package com.biblioteca.controller;

import org.springframework.web.bind.annotation.*;
import com.biblioteca.repository.UsuarioRepository;
import com.biblioteca.model.Usuario;
import java.util.List;  
@RestController
@RequestMapping("/usuarios")
@CrossOrigin

public class UsuarioController{

private final UsuarioRepository repo;

public UsuarioController(UsuarioRepository repo){

this.repo=repo;

}

@PostMapping("/login")

public Usuario login(@RequestBody Usuario usuario){

return repo.findByNombreAndPassword(
usuario.getNombre(),
usuario.getPassword()
);

}

}