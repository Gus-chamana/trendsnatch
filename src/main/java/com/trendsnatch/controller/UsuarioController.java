package com.trendsnatch.controller;

import com.trendsnatch.model.Usuario;
import com.trendsnatch.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/perfil")
    public String verPerfil(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String correo = auth.getName(); // Obtiene el correo del usuario logueado

        Optional<Usuario> usuarioOptional = usuarioService.findByCorreo(correo);
        if (usuarioOptional.isPresent()) {
            model.addAttribute("usuario", usuarioOptional.get());
            // Aquí se cargaría el historial de pedidos, wishlist, etc.
            return "usuario/perfil";
        }
        return "redirect:/login";
    }
}