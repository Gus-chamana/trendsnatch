package com.trendsnatch.controller;

import com.trendsnatch.model.Producto;
import com.trendsnatch.repository.ProductoRepository;
import com.trendsnatch.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoService productoService;

    @GetMapping("/")
    public String home(Model model) {
        // Para "Lo más visto" y "Nuevas prendas", aquí usamos una lista simple.
        // En una app real, esto tendría lógica de negocio más compleja.
        model.addAttribute("productos", productoRepository.findAll());
        return "index"; // Devuelve index.html
    }

    @GetMapping("/explorar")
    public String explorar(Model model,
                           @RequestParam(required = false) String categoria,
                           @RequestParam(required = false, name = "red_social") String redSocial) {

        // 1. Obtenemos la lista de productos ya filtrada desde el servicio
        List<Producto> productosFiltrados = productoService.findByFilters(categoria, redSocial);

        // 2. Pasamos la lista filtrada a la vista
        model.addAttribute("productos", productosFiltrados);

        // 3. Pasamos las listas de filtros disponibles para construir los menús
        model.addAttribute("categorias", productoService.getDistinctCategorias());
        model.addAttribute("redesSociales", productoService.getDistinctRedesSociales());

        // 4. Devolvemos los filtros activos para poder resaltarlos en la vista
        model.addAttribute("categoriaActiva", categoria);
        model.addAttribute("redSocialActiva", redSocial);

        return "explorar";
    }

    @GetMapping("/tendencias")
    public String tendencias() {
        return "tendencias";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}