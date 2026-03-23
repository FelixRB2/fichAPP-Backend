package com.example.proyectoFichaje.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyectoFichaje.models.Usuarios;
import com.example.proyectoFichaje.models.Rol.NombreRol;
import com.example.proyectoFichaje.repository.usuariosRepository;
import com.example.proyectoFichaje.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // POST /api/usuarios
    // Body: { "nombre": "Juan", "apellido1": "García", "apellido2": "López",
    //         "correo": "juan@test.com", "puesto": "Desarrollador", "contrasena": "1234" }
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearUsuarioRequest request) {
        try {
            Usuarios usuario = usuarioService.crear(
                request.nombreRol,
                request.nombre,
                request.apellido1,
                request.apellido2,
                request.correo,
                request.puesto,
                request.contrasena,
                request.horasSemanales
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/usuarios
    @GetMapping
    public ResponseEntity<List<Usuarios>> listarTodos() {
        return ResponseEntity.ok(usuarioService.listarTodos());
    }

    // GET /api/usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrarUsuario(@PathVariable UUID id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.ok().build();
    }

    static class CrearUsuarioRequest {
        public NombreRol nombreRol;
        public String nombre;
        public String apellido1;
        public String apellido2;
        public String correo;
        public String puesto;
        public BigDecimal horasSemanales;
        public String contrasena;
    }
}
