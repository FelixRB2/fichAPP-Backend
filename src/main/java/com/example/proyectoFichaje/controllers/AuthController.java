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
            Usuarios usuario = usuarioService.crear(
                    Rol.NombreRol.Trabajador,
                    request.nombre,
                    request.apellido1,
                    request.apellido2,
                    request.email,
                    request.puesto,
                    request.password,
                    request.horasSemanales
            );
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    static class LoginRequest {
        public String email;
        public String password;
    }

    static class RegisterRequest {
        public String nombre;
        public String apellido1;
        public String apellido2;
        public String email;
        public String puesto;
        public java.math.BigDecimal horasSemanales;
        public String password;
    }
}
