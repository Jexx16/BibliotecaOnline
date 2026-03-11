package com.biblioteca.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import com.biblioteca.repository.UsuarioRepository;
import com.biblioteca.model.Usuario;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin
public class UsuarioController {

    private final UsuarioRepository repo;

    public UsuarioController(UsuarioRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody Usuario usuario) {
        return repo.findByNombreAndPassword(
            usuario.getNombre(),
            usuario.getPassword()
        );
    }

    // ✅ NUEVO: Endpoint para registro
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        System.out.println("=== INTENTO DE REGISTRO ===");
        System.out.println("Nombre: " + usuario.getNombre());
        System.out.println("Password: " + usuario.getPassword());
        
        try {
            // Validar que el nombre no esté vacío
            if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
                return ResponseEntity.status(400).body("El nombre de usuario es obligatorio");
            }
            
            // Validar que la contraseña tenga al menos 6 caracteres
            if (usuario.getPassword() == null || usuario.getPassword().length() < 6) {
                return ResponseEntity.status(400).body("La contraseña debe tener al menos 6 caracteres");
            }
            
            // Verificar si el usuario ya existe
            List<Usuario> existentes = repo.findAll();
            for (Usuario u : existentes) {
                if (u.getNombre().equalsIgnoreCase(usuario.getNombre().trim())) {
                    return ResponseEntity.status(400).body("El nombre de usuario ya existe");
                }
            }
            
            // Asignar tipo por defecto
            usuario.setTipo("usuario");
            
            // Guardar el nuevo usuario
            Usuario nuevo = repo.save(usuario);
            System.out.println("Usuario registrado con ID: " + nuevo.getId());
            
            return ResponseEntity.ok(nuevo);
            
        } catch (Exception e) {
            System.out.println("ERROR en registro: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno al registrar usuario");
        }
    }
}