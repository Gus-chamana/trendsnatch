package com.trendsnatch.controller;

import com.trendsnatch.dto.CarritoItem;
import com.trendsnatch.model.DetallePedido;
import com.trendsnatch.model.Pedido;
import com.trendsnatch.model.Usuario;
import com.trendsnatch.service.PedidoService;
import com.trendsnatch.service.ProductoService;
import com.trendsnatch.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/checkout")
@SessionAttributes("carrito")
public class CheckoutController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String mostrarCheckout(@ModelAttribute("carrito") Map<Integer, CarritoItem> carrito,
                                  Authentication authentication, Model model) {

        if (carrito.isEmpty()) {
            return "redirect:/carrito";
        }

        String correo = authentication.getName();
        Usuario usuario = usuarioService.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        double total = carrito.values().stream().mapToDouble(CarritoItem::getSubtotal).sum();

        model.addAttribute("usuario", usuario);
        model.addAttribute("carritoItems", carrito.values());
        model.addAttribute("total", total);

        return "checkout"; // Devuelve la vista checkout.html
    }

    @PostMapping("/procesar")
    public String procesarPedido(Authentication authentication,
                                 @ModelAttribute("carrito") Map<Integer, CarritoItem> carrito,
                                 SessionStatus status, RedirectAttributes redirectAttributes) {

        if (carrito.isEmpty()) {
            return "redirect:/";
        }

        // 1. Obtener el usuario autenticado
        String correo = authentication.getName();
        Usuario usuario = usuarioService.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        // 2. Crear el objeto Pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstado("PENDIENTE");

        // 3. Crear los detalles del pedido a partir del carrito
        List<DetallePedido> detalles = new ArrayList<>();
        for (CarritoItem item : carrito.values()) {
            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(item.getProducto());
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getProducto().getPrecio());
            detalle.setPedido(pedido);
            detalles.add(detalle);

            // Opcional pero recomendado: Actualizar el stock del producto
            // Producto producto = item.getProducto();
            // producto.setStock(producto.getStock() - item.getCantidad());
            // productoService.save(producto);
        }

        pedido.setDetalles(detalles);

        // 4. Calcular y establecer el total
        double total = detalles.stream()
                .mapToDouble(d -> d.getPrecioUnitario() * d.getCantidad())
                .sum();
        pedido.setTotal(total);

        // 5. Guardar el pedido en la base de datos
        Pedido pedidoGuardado = pedidoService.save(pedido);

        // 6. Limpiar el carrito de la sesión
        status.setComplete();

        // 7. Redirigir a la página de confirmación
        redirectAttributes.addAttribute("pedidoId", pedidoGuardado.getId());
        return "redirect:/checkout/confirmacion";
    }

    @GetMapping("/confirmacion")
    public String mostrarConfirmacion(@RequestParam("pedidoId") Integer pedidoId, Model model) {
        Pedido pedido = pedidoService.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        model.addAttribute("pedido", pedido);
        return "confirmacion-compra"; // Devuelve la vista confirmacion-compra.html
    }
}