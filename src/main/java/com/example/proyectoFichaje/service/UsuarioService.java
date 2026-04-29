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
    private final EmailService emailService;

    public UsuarioService(usuariosRepository usuarioRepo, rolRepository rolRepo, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
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

        Rol rol = rolRepo.findByNombreRol(nombreRol)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol + ". Ejecuta primero el SQL de roles."));

        Usuarios usuario = new Usuarios();
        usuario.setNombre(nombre);
        usuario.setApellido1(apellido1);
        usuario.setApellido2(apellido2);
        usuario.setCorreo(correo);
        usuario.setPuesto(puesto);
        usuario.setHorasSemanales(horasSemanales);
        usuario.setContrasena(passwordEncoder.encode(contrasena));
        usuario.setRol(rol);
        usuario.setEstado(Usuarios.EstadoUsuario.activo);
        
        Usuarios nuevoUsuario = usuarioRepo.save(usuario);

        // Enviar correo de bienvenida (Asíncrono)
        emailService.enviarCorreoBienvenida(correo, nombre, apellido1);

        return nuevoUsuario;
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

    public Usuarios actualizarEstado(UUID id, Usuarios.EstadoUsuario nuevoEstado) {
        Usuarios usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));
        usuario.setEstado(nuevoEstado);
        return usuarioRepo.save(usuario);
    }

    public Usuarios actualizar(UUID id, NombreRol nombreRol, String nombre, String apellido1, String apellido2,
                               String correo, String puesto, BigDecimal horasSemanales, String contrasena, Usuarios.EstadoUsuario estado) {
        Usuarios usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + id));

        Rol rol = rolRepo.findByNombreRol(nombreRol)
            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol));

        usuario.setNombre(nombre);
        usuario.setApellido1(apellido1);
        usuario.setApellido2(apellido2);
        usuario.setCorreo(correo);
        usuario.setPuesto(puesto);
        usuario.setHorasSemanales(horasSemanales);
        usuario.setContrasena(contrasena);
        usuario.setRol(rol);
        usuario.setEstado(estado);
        return usuarioRepo.save(usuario);
    }

    public void eliminarUsuario(UUID id) {
        Usuarios usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado: " + id));

    
        usuarioRepo.delete(usuario);
    }
}
