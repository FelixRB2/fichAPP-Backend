package com.example.proyectoFichaje.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.proyectoFichaje.models.Rol;
import com.example.proyectoFichaje.models.Usuarios;
import com.example.proyectoFichaje.models.Rol.NombreRol;
import com.example.proyectoFichaje.repository.rolRepository;
import com.example.proyectoFichaje.repository.usuariosRepository;

@Service
public class UsuarioService implements UserDetailsService {

    private final usuariosRepository usuarioRepo;
    private final rolRepository rolRepo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(usuariosRepository usuarioRepo, rolRepository rolRepo, PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepo.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + username));
    }

    // CREAR usuario trabajador
    public Usuarios crear(NombreRol nombreRol, String nombre, String apellido1, String apellido2,
                          String correo, String puesto, String contrasena, BigDecimal horasSemanales) {

        if (usuarioRepo.existsByCorreo(correo)) {
            throw new RuntimeException("Ya existe un usuario con el correo: " + correo);
        }

        Rol rolTrabajador = rolRepo.findByNombreRol(Rol.NombreRol.Trabajador)
            .orElseThrow(() -> new RuntimeException("Rol Trabajador no encontrado. Ejecuta primero el SQL de roles."));

        Usuarios usuario = new Usuarios();
        usuario.setNombre(nombre);
        usuario.setApellido1(apellido1);
        usuario.setApellido2(apellido2);
        usuario.setCorreo(correo);
        usuario.setPuesto(puesto);
        usuario.setHorasSemanales(horasSemanales);
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        usuario.setRol(rolTrabajador);
        usuario.setEstado(Usuarios.EstadoUsuario.activo);
        return usuarioRepo.save(usuario);
    }

    // READ: obtener usuario por id
    public Usuarios obtenerPorId(UUID id) {
        return usuarioRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
    }

    // READ: listar todos
    public List<Usuarios> listarTodos() {
        return usuarioRepo.findAll();
    }

    
    public void eliminarUsuario(UUID id) {
        Usuarios usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado: " + id));

    
        usuarioRepo.delete(usuario);
    }
}
