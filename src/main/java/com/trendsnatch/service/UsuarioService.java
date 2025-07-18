package com.trendsnatch.service;

import com.trendsnatch.model.Rol;
import com.trendsnatch.model.Usuario;
import com.trendsnatch.repository.RolRepository;
import com.trendsnatch.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- MÉTODO PARA CAMBIAR LA CONTRASEÑA DE UN USUARIO NORMAL ---
    @Transactional
    public boolean cambiarContrasenaUsuario(Integer usuarioId, String passActual, String passNueva) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificamos si la contraseña actual que ingresó el usuario coincide con la de la BD
        if (passwordEncoder.matches(passActual, usuario.getContrasena())) {
            // Si coincide, encriptamos y guardamos la nueva contraseña
            usuario.setContrasena(passwordEncoder.encode(passNueva));
            usuarioRepository.save(usuario);
            return true; // Éxito
        }
        return false; // Fracaso (contraseña actual incorrecta)
    }

    // --- MÉTODO PARA ACTUALIZAR DATOS DEL USUARIO NORMAL ---
    @Transactional
    public void actualizarDatosUsuario(Integer usuarioId, String nuevoNombre) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setNombre(nuevoNombre);
        usuarioRepository.save(usuario);
    }

    // --- MÉTODO PARA ACTUALIZAR DATOS DEL ADMIN ---
    @Transactional
    public void actualizarDatosAdmin(Integer adminId, String nuevoNombre) {
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));
        admin.setNombre(nuevoNombre);
        usuarioRepository.save(admin);
    }

    // --- MÉTODO PARA CAMBIAR LA CONTRASEÑA DEL ADMIN ---
    @Transactional
    public boolean cambiarContrasenaAdmin(Integer adminId, String passActual, String passNueva) {
        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

        // Verificamos si la contraseña actual coincide con la guardada en la BD
        if (passwordEncoder.matches(passActual, admin.getContrasena())) {
            // Si coincide, encriptamos y guardamos la nueva contraseña
            admin.setContrasena(passwordEncoder.encode(passNueva));
            usuarioRepository.save(admin);
            return true; // Éxito
        }
        return false; // Fracaso (contraseña actual incorrecta)
    }


    // --- MÉTODO PARA CREAR USUARIOS DESDE EL PANEL DE ADMIN ---
    @Transactional
    public void crearUsuarioPorAdmin(Usuario usuario) {
        // Encriptamos la contraseña antes de guardarla
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        usuarioRepository.save(usuario);
    }

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