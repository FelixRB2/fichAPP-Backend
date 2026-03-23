package com.example.proyectoFichaje.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyectoFichaje.models.Fichajes;
import com.example.proyectoFichaje.service.FichajeService;

@RestController
@RequestMapping("/api/fichajes")
public class FichajeController {

    private final FichajeService fichajeService;

    public FichajeController(FichajeService fichajeService) {
        this.fichajeService = fichajeService;
    }

    // POST /api/fichajes
    // Body: { "idUsuario": "...", "horaEntrada": "09:00", "horaSalida": "18:00", "fecha": "2025-01-13", "comentario": "..." }
    @PostMapping
    public ResponseEntity<?> registrarEntrada(@RequestBody EntradaRequest request) {
        try {
            Fichajes fichaje = fichajeService.registrarEntrada(request.idUsuario, request.comentario);
            return ResponseEntity.status(HttpStatus.CREATED).body(fichaje);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/fichajes/{id}/salida
    @PutMapping("/{id}/salida")
    public ResponseEntity<?> registrarSalida(@PathVariable UUID id) {
        try {
            Fichajes fichaje = fichajeService.registrarSalida(id);
            return ResponseEntity.ok(fichaje);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET /api/fichajes/usuario/{idUsuario}
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> obtenerPorUsuario(@PathVariable UUID idUsuario) {
        try {
            List<Fichajes> fichajes = fichajeService.obtenerPorUsuario(idUsuario);
            return ResponseEntity.ok(fichajes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // PUT /api/fichajes/{id}
    // Body: { "horaEntrada": "09:00:00", "horaSalida": "18:00:00", "comentario": "Corrección" }
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable UUID id, @RequestBody ActualizarRequest request) {
        try {
            Fichajes fichaje = fichajeService.actualizar(
                id,
                request.horaEntrada,
                request.horaSalida,
                request.comentario
            );
            return ResponseEntity.ok(fichaje);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // -- DTOs internos --

    static class CrearFichajeRequest {
        public UUID idUsuario;
        public LocalTime horaEntrada;
        public LocalTime horaSalida;
        public LocalDate fecha;
        public String comentario;
    }

    static class ActualizarFichajeRequest {
        public LocalTime horaEntrada;
        public LocalTime horaSalida;
        public String comentario;
    }

    static class EntradaRequest {
        public UUID idUsuario;
        public String comentario;
    }

    static class ActualizarRequest {
        public LocalTime horaEntrada;
        public LocalTime horaSalida;
        public String comentario;
    }
    
}
