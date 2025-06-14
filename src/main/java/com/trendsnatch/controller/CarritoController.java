package com.trendsnatch.controller;

import com.trendsnatch.dto.CarritoItem;
import com.trendsnatch.model.Producto;
import com.trendsnatch.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/carrito")
@SessionAttributes("carrito")
public class CarritoController {

    @Autowired
    private ProductoService productoService;

    // Inicializa el carrito en la sesión si no existe
    @ModelAttribute("carrito")
    public Map<Integer, CarritoItem> getCarrito() {
        return new HashMap<>();
    }

    @GetMapping
    public String verCarrito(@ModelAttribute("carrito") Map<Integer, CarritoItem> carrito, Model model) {
        double total = carrito.values().stream().mapToDouble(CarritoItem::getSubtotal).sum();
        model.addAttribute("carritoItems", carrito.values());
        model.addAttribute("total", total);
        return "carrito"; // Devuelve la vista carrito.html
    }

    @PostMapping("/agregar")
    public String agregarAlCarrito(@RequestParam("productoId") Integer productoId,
                                   @RequestParam("cantidad") int cantidad,
                                   @ModelAttribute("carrito") Map<Integer, CarritoItem> carrito) {

        Producto producto = productoService.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto inválido:" + productoId));

        if (carrito.containsKey(productoId)) {
            // Si el producto ya está en el carrito, actualiza la cantidad
            CarritoItem item = carrito.get(productoId);
            item.setCantidad(item.getCantidad() + cantidad);
        } else {
            // Si es un producto nuevo, lo añade al carrito
            carrito.put(productoId, new CarritoItem(producto, cantidad));
        }

        return "redirect:/carrito";
    }

    @GetMapping("/remover/{productoId}")
    public String removerDelCarrito(@PathVariable("productoId") Integer productoId,
                                    @ModelAttribute("carrito") Map<Integer, CarritoItem> carrito) {
        carrito.remove(productoId);
        return "redirect:/carrito";
    }

    @GetMapping("/vaciar")
    public String vaciarCarrito(SessionStatus status) {
        // Marca la sesión del carrito como completada, eliminándola
        status.setComplete();
        return "redirect:/carrito";
    }
}
