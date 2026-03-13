package com.biblioteca.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VistaController {

    @GetMapping("/catalogo")
    public String catalogo() {
        return "forward:/libros.html";
    }
}