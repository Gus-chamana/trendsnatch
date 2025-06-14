package com.trendsnatch.controller;

import com.trendsnatch.model.Producto;
import com.trendsnatch.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/producto")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/{id}")
    public String verProducto(@PathVariable Integer id, Model model) {
        Optional<Producto> productoOptional = productoService.findById(id);
        if (productoOptional.isPresent()) {
            model.addAttribute("producto", productoOptional.get());
            // Lógica para productos relacionados (aquí una simple muestra)
            model.addAttribute("relacionados", productoService.findAll().stream().limit(4).toList());
            return "producto"; // Devuelve producto.html
        }
        return "redirect:/"; // Si no lo encuentra, redirige al inicio
    }
}