package com.biblioteca.model;

import jakarta.persistence.*;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String password;
    private String tipo;
    private String rol;  // ← Este campo debe existir

    // Getters y Setters de TODOS los campos
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    // ⚠️ ESTOS SON LOS QUE FALTAN - AGREGALOS:
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}