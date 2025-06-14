package com.trendsnatch.repository;

import com.trendsnatch.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByCorreo(String correo);

    long countByActivo(boolean activo);
    List<Usuario> findByActivo(boolean activo);
}