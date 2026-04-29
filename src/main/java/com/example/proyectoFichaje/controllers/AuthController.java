package com.example.proyectoFichaje.controllers;

import com.example.proyectoFichaje.models.Rol;
import com.example.proyectoFichaje.models.Usuarios;
import com.example.proyectoFichaje.service.AuthService;
import com.example.proyectoFichaje.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Para desarrollo con Angular
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authService.authenticate(request.email, request.password);
            Usuarios usuario = (Usuarios) usuarioService.loadUserByUsername(request.email);
            String token = authService.generateToken(usuario);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("rol", usuario.getRol().getNombreRol().name());
            response.put("nombre", usuario.getNombre());
            response.put("id", usuario.getIdUsuario());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            System.out.println("Intentando registrar usuario: " + request.email);
            
            Rol.NombreRol rolToSet = Rol.NombreRol.Trabajador;
            if (request.nombreRol != null) {
                try {
                    rolToSet = Rol.NombreRol.valueOf(request.nombreRol);
                } catch (IllegalArgumentException e) {
                    System.out.println("Rol no válido recibido: " + request.nombreRol);
                }
            }

            Usuarios usuario = usuarioService.crear(
                    rolToSet,
                    request.nombre,
                    request.apellido1,
                    request.apellido2,
                    request.email,
                    request.puesto,
                    request.contrasena,
                    request.horasSemanales
            );
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            System.err.println("Error en el registro: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    static class LoginRequest {
        public String email;
        public String password;
    }

    static class RegisterRequest {
        @com.fasterxml.jackson.annotation.JsonProperty("nombreRol")
        public String nombreRol;
        @com.fasterxml.jackson.annotation.JsonProperty("nombre")
        public String nombre;
        @com.fasterxml.jackson.annotation.JsonProperty("apellido1")
        public String apellido1;
        @com.fasterxml.jackson.annotation.JsonProperty("apellido2")
        public String apellido2;
        @com.fasterxml.jackson.annotation.JsonProperty("email")
        public String email;
        @com.fasterxml.jackson.annotation.JsonProperty("puesto")
        public String puesto;
        @com.fasterxml.jackson.annotation.JsonProperty("horasSemanales")
        public java.math.BigDecimal horasSemanales;
        @com.fasterxml.jackson.annotation.JsonProperty("contrasena")
        public String contrasena;
    }
}
