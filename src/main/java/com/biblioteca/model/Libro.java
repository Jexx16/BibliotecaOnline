package com.biblioteca.model;

import jakarta.persistence.*;

@Entity
public class Libro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titulo;
    private String autor;
    private String editorial;
    private Integer cantidad;              // Total de copias
    private Integer cantidadDisponible;    // Copias disponibles ahora
    private boolean disponible;             // true si cantidadDisponible > 0
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    
    public Integer getCantidadDisponible() { return cantidadDisponible; }
    public void setCantidadDisponible(Integer cantidadDisponible) { 
        this.cantidadDisponible = cantidadDisponible;
        // Actualizar disponible automáticamente
        this.disponible = cantidadDisponible > 0;
    }
    
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { 
        // Este setter ya no debería usarse directamente
        this.disponible = disponible;
    }
}