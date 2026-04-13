package com.example.proyectoFichaje.controllers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.example.proyectoFichaje.models.Usuarios;
import com.example.proyectoFichaje.models.Rol.NombreRol;
import com.example.proyectoFichaje.repository.usuariosRepository;
import com.example.proyectoFichaje.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
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
                request.getNombreRol(),
                request.getNombre(),
                request.getApellido1(),
                request.getApellido2(),
                request.getCorreo(),
                request.getPuesto(),
                request.getContrasena(),
                request.getHorasSemanales()
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

    @org.springframework.web.bind.annotation.PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable UUID id, @RequestBody ActualizarUsuarioRequest request) {
        try {
            com.example.proyectoFichaje.models.Usuarios.EstadoUsuario nuevoEstado = 
                request.isActivo() ? com.example.proyectoFichaje.models.Usuarios.EstadoUsuario.activo : 
                                    com.example.proyectoFichaje.models.Usuarios.EstadoUsuario.suspendido;

            Usuarios usuario = usuarioService.actualizar(
                id,
                request.getNombreRol(),
                request.getNombre(),
                request.getApellido1(),
                request.getApellido2(),
                request.getCorreo(),
                request.getPuesto(),
                request.getHorasSemanales(),
                request.getContrasena(),
                nuevoEstado
            );
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public static class ActualizarUsuarioRequest {
        private NombreRol nombreRol;
        private String nombre;
        private String apellido1;
        private String apellido2;
        private String correo;
        private String puesto;
        private java.math.BigDecimal horasSemanales;
        private String contrasena;
        private boolean activo;

        public ActualizarUsuarioRequest() {}

        public NombreRol getNombreRol() { return nombreRol; }
        public void setNombreRol(NombreRol nombreRol) { this.nombreRol = nombreRol; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getApellido1() { return apellido1; }
        public void setApellido1(String apellido1) { this.apellido1 = apellido1; }
        public String getApellido2() { return apellido2; }
        public void setApellido2(String apellido2) { this.apellido2 = apellido2; }
        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getPuesto() { return puesto; }
        public void setPuesto(String puesto) { this.puesto = puesto; }
        public java.math.BigDecimal getHorasSemanales() { return horasSemanales; }
        public void setHorasSemanales(java.math.BigDecimal horasSemanales) { this.horasSemanales = horasSemanales; }
        public String getContrasena() {return contrasena; }
        public void setContrasena(String contrasena) {this.contrasena = contrasena; }

        public boolean isActivo() { return activo; }
        public void setActivo(boolean activo) { this.activo = activo; }
    }

    @org.springframework.web.bind.annotation.PatchMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable UUID id, @RequestBody PatchEstadoRequest request) {
        try {
            com.example.proyectoFichaje.models.Usuarios.EstadoUsuario nuevoEstado = 
                request.isActivo() ? com.example.proyectoFichaje.models.Usuarios.EstadoUsuario.activo : 
                                    com.example.proyectoFichaje.models.Usuarios.EstadoUsuario.suspendido;
            return ResponseEntity.ok(usuarioService.actualizarEstado(id, nuevoEstado));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public static class PatchEstadoRequest {
        private boolean activo;
        public boolean isActivo() { return activo; }
        public void setActivo(boolean activo) { this.activo = activo; }
    }

    public static class CrearUsuarioRequest {
        @JsonProperty("nombreRol")
        private NombreRol nombreRol;
        
        @JsonProperty("nombre")
        private String nombre;
        
        @JsonProperty("apellido1")
        private String apellido1;
        
        @JsonProperty("apellido2")
        private String apellido2;
        
        @JsonProperty("correo")
        private String correo;
        
        @JsonProperty("puesto")
        private String puesto;
        
        @JsonProperty("horasSemanales")
        private BigDecimal horasSemanales;
        
        @JsonProperty("contrasena")
        private String contrasena;

        public CrearUsuarioRequest() {}

        // Getters and Setters
        public NombreRol getNombreRol() { return nombreRol; }
        public void setNombreRol(NombreRol nombreRol) { this.nombreRol = nombreRol; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getApellido1() { return apellido1; }
        public void setApellido1(String apellido1) { this.apellido1 = apellido1; }
        public String getApellido2() { return apellido2; }
        public void setApellido2(String apellido2) { this.apellido2 = apellido2; }
        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getPuesto() { return puesto; }
        public void setPuesto(String puesto) { this.puesto = puesto; }
        public BigDecimal getHorasSemanales() { return horasSemanales; }
        public void setHorasSemanales(BigDecimal horasSemanales) { this.horasSemanales = horasSemanales; }
        public String getContrasena() { return contrasena; }
        public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    }
}
