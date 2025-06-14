package com.trendsnatch.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * Esta clase se encarga de decidir a qué página redirigir al usuario después de un inicio de sesión exitoso,
 * basándose en su rol (ROLE_ADMIN o ROLE_USER).
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    // Usamos una estrategia de redirección para enviar al usuario a la URL correcta.
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        handle(request, response, authentication);
        clearAuthenticationAttributes(request);
    }

    protected void handle(HttpServletRequest request,
                          HttpServletResponse response,
                          Authentication authentication) throws IOException {

        // Obtenemos la URL de destino basada en el rol del usuario.
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            // Si la respuesta ya se ha enviado, no hacemos nada.
            return;
        }

        // Redirigimos al usuario a la URL de destino.
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    /**
     * Este método lee los roles (authorities) del usuario autenticado y devuelve
     * la URL apropiada.
     */
    protected String determineTargetUrl(final Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Verificamos si la colección de roles contiene ROLE_ADMIN.
        if (authorities.stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))) {
            // Si es un administrador, lo redirigimos a su perfil de administrador.
            return "/admin/perfil";
        }
        // Verificamos si la colección de roles contiene ROLE_USER.
        else if (authorities.stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"))) {
            // Si es un usuario normal, lo redirigimos a su perfil de usuario.
            return "/usuario/perfil";
        }

        // Si por alguna razón no tiene un rol conocido, lanzamos una excepción.
        throw new IllegalStateException("El usuario no tiene un rol válido para la redirección.");
    }

    /**
     * Limpia los atributos de autenticación temporales de la sesión.
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}