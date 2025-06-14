package com.trendsnatch.service;

import com.trendsnatch.model.Rol;
import com.trendsnatch.model.Usuario;
import com.trendsnatch.repository.RolRepository;
import com.trendsnatch.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository; // Necesario para buscar roles

    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional
    public void cambiarRol(Integer usuarioId, Long rolId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Rol rol = rolRepository.findById(rolId).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        usuario.setRol(rol);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public String toggleActivo(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setActivo(!usuario.isActivo());
        usuarioRepository.save(usuario);
        return usuario.isActivo() ? "Usuario activado correctamente." : "Usuario bloqueado correctamente.";
    }

    @Transactional
    public void deleteById(Integer usuarioId) {
        usuarioRepository.deleteById(usuarioId);
    }

    // --- MÉTODOS NUEVOS PARA EL DASHBOARD ---

    /**
     * Cuenta los usuarios según su estado (activos/inactivos).
     * @param activo true para activos, false para inactivos.
     * @return El total de usuarios.
     */
    public long countByActivo(boolean activo) {
        return usuarioRepository.countByActivo(activo);
    }

    /**
     * Encuentra todos los usuarios inactivos (bloqueados).
     * @param activo El estado (siempre será false).
     * @return Una lista de usuarios inactivos.
     */
    public List<Usuario> findByActivo(boolean activo) {
        return usuarioRepository.findByActivo(activo);
    }

}