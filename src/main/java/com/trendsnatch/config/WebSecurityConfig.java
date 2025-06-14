package com.trendsnatch.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    // 1. Inyectamos nuestro nuevo manejador personalizado que acabamos de crear.
    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/", "/explorar", "/producto/**", "/tendencias", "/nosotros", "/css/**", "/js/**", "/images/**", "/registro", "/login").permitAll()
                        // Rutas de usuario
                        .requestMatchers("/usuario/**", "/carrito", "/checkout").hasRole("USER")
                        // Rutas de administrador
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login-proc")
                        // 2. HEMOS ELIMINADO .defaultSuccessUrl(...) DE AQUÍ
                        // 3. AHORA USAMOS NUESTRO MANEJADOR PERSONALIZADO
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")
                        .permitAll()
                )
                .exceptionHandling(e -> e.accessDeniedPage("/acceso-denegado"));

        return http.build();
    }
}