package com.biblioteca.model;

import jakarta.persistence.*;

@Entity
public class Libro{

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private String titulo;
private String autor;
private String editorial;
private boolean disponible;

public Long getId(){return id;}

public String getTitulo(){return titulo;}
public void setTitulo(String titulo){this.titulo=titulo;}

public String getAutor(){return autor;}
public void setAutor(String autor){this.autor=autor;}

public String getEditorial(){return editorial;}
public void setEditorial(String editorial){this.editorial=editorial;}

public boolean isDisponible(){return disponible;}
public void setDisponible(boolean disponible){this.disponible=disponible;}

}