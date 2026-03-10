package com.biblioteca.model;

import jakarta.persistence.*;

@Entity
public class Usuario{

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String nombre;
private String password;
private String tipo;

public Long getId(){return id;}

public String getNombre(){return nombre;}
public void setNombre(String nombre){this.nombre=nombre;}

public String getPassword(){return password;}
public void setPassword(String password){this.password=password;}

public String getTipo(){return tipo;}
public void setTipo(String tipo){this.tipo=tipo;}

}