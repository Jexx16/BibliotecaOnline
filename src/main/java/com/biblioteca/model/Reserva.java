package com.biblioteca.model;

import jakarta.persistence.*;

@Entity
public class Reserva{

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

private Long usuarioId;
private Long libroId;
private String fecha;

public Long getId(){return id;}

public Long getUsuarioId(){return usuarioId;}
public void setUsuarioId(Long usuarioId){this.usuarioId=usuarioId;}

public Long getLibroId(){return libroId;}
public void setLibroId(Long libroId){this.libroId=libroId;}

public String getFecha(){return fecha;}
public void setFecha(String fecha){this.fecha=fecha;}

}