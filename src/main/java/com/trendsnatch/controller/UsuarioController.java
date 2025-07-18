package com.trendsnatch.controller;

import com.trendsnatch.model.Pedido;
import com.trendsnatch.model.Usuario;
import com.trendsnatch.service.PedidoService;
import com.trendsnatch.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/perfil")
    public String verPerfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName(); // Obtiene el correo del usuario logueado

        Optional<Usuario> usuarioOptional = usuarioService.findByCorreo(correo);
        if (usuarioOptional.isPresent()) {
            // 1. Obtenemos el objeto Usuario y lo guardamos en una variable.
            Usuario usuario = usuarioOptional.get();

            // 2. Usamos esa variable para añadir los datos del usuario al modelo.
            model.addAttribute("usuario", usuario);
            // 3. Usamos LA MISMA variable para buscar su historial de pedidos.
            model.addAttribute("usuario", usuarioOptional.get());
            List<Pedido> historialPedidos = pedidoService.findByUsuario(usuario);
            model.addAttribute("pedidos", historialPedidos);
            return "usuario/perfil";
        }
        return "redirect:/login";
    }

    // --- MÉTODO PARA ACTUALIZAR DATOS DEL USUARIO ---
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfilUsuario(@RequestParam("nombre") String nuevoNombre,
                                          Authentication authentication,
                                          RedirectAttributes redirectAttributes) {

        String correo = authentication.getName();
        Usuario usuario = usuarioService.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuarioService.actualizarDatosUsuario(usuario.getId(), nuevoNombre);

        redirectAttributes.addFlashAttribute("successMessage", "¡Tus datos han sido actualizados!");
        return "redirect:/usuario/perfil";
    }

    // --- MÉTODO PARA CAMBIAR LA CONTRASEÑA DEL USUARIO ---
    @PostMapping("/perfil/cambiar-contrasena")
    public String cambiarContrasenaUsuario(Authentication authentication,
                                           @RequestParam("pass-actual") String passActual,
                                           @RequestParam("pass-nueva") String passNueva,
                                           @RequestParam("pass-confirmar") String passConfirmar,
                                           RedirectAttributes redirectAttributes) {

        if (!passNueva.equals(passConfirmar)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Las contraseñas nuevas no coinciden.");
            return "redirect:/usuario/perfil";
        }

        String correo = authentication.getName();
        Usuario usuario = usuarioService.findByCorreo(correo).get();

        boolean exito = usuarioService.cambiarContrasenaUsuario(usuario.getId(), passActual, passNueva);

        if (exito) {
            redirectAttributes.addFlashAttribute("successMessage", "¡Contraseña cambiada correctamente!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "La contraseña actual es incorrecta.");
        }

        return "redirect:/usuario/perfil";
    }

}