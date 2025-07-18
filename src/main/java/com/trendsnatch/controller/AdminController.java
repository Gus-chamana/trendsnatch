package com.trendsnatch.controller;

import com.trendsnatch.model.Producto;
import com.trendsnatch.model.Rol;
import com.trendsnatch.model.Usuario;
import com.trendsnatch.repository.DetallePedidoRepository;
import com.trendsnatch.repository.RolRepository;
import com.trendsnatch.service.PedidoService;
import com.trendsnatch.service.ProductoService;
import com.trendsnatch.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.Authentication;



@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PedidoService pedidoService; // Se usaría para la gestión de pedidos

    @Autowired
    private RolRepository rolRepository; // Necesario para obtener la lista de roles

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    // --- MÉTODO PARA PROCESAR LA CREACIÓN DE UN USUARIO ---
    @PostMapping("/usuarios/crear")
    public String crearUsuario(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        // Aquí puedes añadir validaciones, como verificar si el correo ya existe

        usuarioService.crearUsuarioPorAdmin(usuario);
        redirectAttributes.addFlashAttribute("successMessage", "¡Usuario creado correctamente!");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // --- Datos para las tarjetas principales ---
        model.addAttribute("totalProductosVisibles", productoService.countByVisible(true));
        model.addAttribute("totalUsuariosActivos", usuarioService.countByActivo(true));
        model.addAttribute("pedidosHoy", pedidoService.countPedidosHoy());

        // --- Datos para los widgets adicionales ---
        model.addAttribute("productosBajoStock", productoService.findByStockLessThan(5)); // Productos con stock < 5
        model.addAttribute("gananciasTotales", pedidoService.findTotalGanancias());
        model.addAttribute("productosOcultos", productoService.findByVisible(false));
        model.addAttribute("usuariosInactivos", usuarioService.findByActivo(false));
        model.addAttribute("pedidosPorEstado", pedidoService.countPedidosPorEstado());
        model.addAttribute("top5Productos", detallePedidoRepository.findTop5ProductosMasVendidos(org.springframework.data.domain.PageRequest.of(0, 5)));
        model.addAttribute("productosPorRedSocial", productoService.countByRedSocialOrigen());

        // --- Para el Timeline de Actividad Reciente (simulado) ---
        model.addAttribute("actividadRecientePedidos", pedidoService.findTop5ByOrderByFechaPedidoDesc());

        return "admin/dashboard";
    }

    @GetMapping("/productos")
    public String gestionProductos(Model model) {
        model.addAttribute("productos", productoService.findAll());
        model.addAttribute("producto", new Producto()); // Para el formulario de 'Crear'
        return "admin/productos";
    }

    @PostMapping("/productos/crear")
    public String crearProducto(@ModelAttribute Producto producto) {
        productoService.save(producto);
        return "redirect:/admin/productos";
    }

    // Método GET actualizado para pasar la lista de roles al modelo
    @GetMapping("/usuarios")
    public String gestionUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.findAll());
        model.addAttribute("roles", rolRepository.findAll());
        model.addAttribute("nuevoUsuario", new Usuario());   // Pasa la lista de roles para el modal
        return "admin/usuarios";
    }

    // Método POST para cambiar el rol de un usuario
    @PostMapping("/usuarios/cambiar-rol")
    public String cambiarRol(@RequestParam("usuarioId") Integer usuarioId,
                             @RequestParam("rolId") Long rolId,
                             RedirectAttributes redirectAttributes) {
        usuarioService.cambiarRol(usuarioId, rolId);
        redirectAttributes.addFlashAttribute("successMessage", "¡Rol actualizado correctamente!");
        return "redirect:/admin/usuarios";
    }

    // Método POST para activar/desactivar un usuario
    @PostMapping("/usuarios/toggle-bloqueo/{id}")
    public String toggleBloqueoUsuario(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        String message = usuarioService.toggleActivo(id);
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:/admin/usuarios";
    }

    // Método POST para eliminar un usuario
    @PostMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "¡Usuario eliminado correctamente!");
        } catch (DataIntegrityViolationException e) {
            // Esto ocurre si el usuario tiene pedidos asociados
            redirectAttributes.addFlashAttribute("errorMessage", "Error: No se puede eliminar el usuario porque tiene pedidos asociados.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el usuario.");
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/pedidos")
    public String gestionPedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.findAll());
        return "admin/pedidos";
    }

    // método para procesar la actualización de estado del pedido
    @PostMapping("/pedidos/actualizar-estado")
    public String actualizarEstadoPedido(@RequestParam("pedidoId") Integer pedidoId,
                                         @RequestParam("nuevoEstado") String nuevoEstado,
                                         RedirectAttributes redirectAttributes) {
        try {
            pedidoService.actualizarEstado(pedidoId, nuevoEstado);
            redirectAttributes.addFlashAttribute("successMessage", "¡Estado del pedido #" + pedidoId + " actualizado correctamente!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar el estado del pedido.");
        }
        // Esta redirección es la clave. Fuerza al navegador a recargar la página.
        return "redirect:/admin/pedidos";
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Integer id) {
        // Llama al servicio para borrar el producto por su ID
        productoService.deleteById(id);
        // se redirige de vuelta a la lista de productos
        return "redirect:/admin/productos";
    }

    // MÉTODO 1: Muestra la página con el formulario de edición
    @GetMapping("/productos/editar/{id}")
    public String mostrarFormularioDeEdicion(@PathVariable("id") Integer id, Model model) {
        // Busca el producto en la base de datos
        Producto producto = productoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de producto inválido:" + id));

        // Añade el producto al modelo para que el formulario lo pueda usar
        model.addAttribute("producto", producto);

        // Devuelve el nombre de la nueva plantilla HTML que crearemos
        return "admin/editar-producto";
    }

    // MÉTODO 2: Procesa los datos del formulario de edición
    @PostMapping("/productos/editar/{id}")
    public String actualizarProducto(@PathVariable("id") Integer id,
                                     @ModelAttribute("producto") Producto producto) {
        // Aseguramos que el ID del objeto sea el correcto
        producto.setId(id);
        // El método save() de JPA actualiza el registro si el ID ya existe
        productoService.save(producto);
        return "redirect:/admin/productos";
    }

    @GetMapping("/perfil")
    public String verPerfilAdmin(Authentication authentication, Model model) {
        // Obtenemos el correo del usuario autenticado
        String correo = authentication.getName();

        // Buscamos el usuario en la base de datos
        Usuario admin = usuarioService.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        // Lo pasamos al modelo para que la vista lo use
        model.addAttribute("admin", admin);

        // Devolvemos el nombre de la nueva plantilla HTML que crearemos
        return "admin/perfil";
    }

    // --- MÉTODO NUEVO PARA ACTUALIZAR DATOS ---
    @PostMapping("/perfil/actualizar-datos")
    public String actualizarDatosAdmin(Authentication authentication,
                                       @RequestParam("nombre") String nuevoNombre,
                                       RedirectAttributes redirectAttributes) {
        String correo = authentication.getName();
        Usuario admin = usuarioService.findByCorreo(correo).get();

        usuarioService.actualizarDatosAdmin(admin.getId(), nuevoNombre);

        redirectAttributes.addFlashAttribute("successMessage", "¡Datos actualizados correctamente!");
        return "redirect:/admin/perfil";
    }

    // --- MÉTODO NUEVO PARA CAMBIAR CONTRASEÑA ---
    @PostMapping("/perfil/cambiar-contrasena")
    public String cambiarContrasenaAdmin(Authentication authentication,
                                         @RequestParam("pass-actual") String passActual,
                                         @RequestParam("pass-nueva") String passNueva,
                                         @RequestParam("pass-confirmar") String passConfirmar,
                                         RedirectAttributes redirectAttributes) {

        if (!passNueva.equals(passConfirmar)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Las contraseñas nuevas no coinciden.");
            return "redirect:/admin/perfil#seguridad";
        }

        String correo = authentication.getName();
        Usuario admin = usuarioService.findByCorreo(correo).get();

        boolean exito = usuarioService.cambiarContrasenaAdmin(admin.getId(), passActual, passNueva);

        if (exito) {
            redirectAttributes.addFlashAttribute("successMessage", "¡Contraseña cambiada correctamente!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "La contraseña actual es incorrecta.");
        }

        return "redirect:/admin/perfil#seguridad";
    }

}