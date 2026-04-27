package com.example.proyectoFichaje.controllers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyectoFichaje.models.Horarios;
import com.example.proyectoFichaje.service.HorarioService;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin(origins = "*")
public class HorarioController {

    private final HorarioService horarioService;

    public HorarioController(HorarioService horarioService) {
        this.horarioService = horarioService;
    }

    @PostMapping
    public ResponseEntity<?> asignarHorario(@RequestBody HorarioRequest request) {
        try {
            Horarios horario = horarioService.asignarHorario(
                request.idUsuario,
                request.nombre,
                request.horaEntrada,
                request.horaSalida,
                request.diasLaborables,
                request.fechaInicio != null ? request.fechaInicio : LocalDate.now(),
                request.fechaFin
            );
            return ResponseEntity.ok(horario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Horarios>> listarPorUsuario(@PathVariable UUID idUsuario) {
        return ResponseEntity.ok(horarioService.listarPorUsuario(idUsuario));
    }

    @GetMapping("/usuario/{idUsuario}/vigente")
    public ResponseEntity<List<Horarios>> obtenerVigente(@PathVariable UUID idUsuario) {
        return ResponseEntity.ok(horarioService.obtenerHorariosVigentes(idUsuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        horarioService.eliminarHorario(id);
        return ResponseEntity.ok().build();
    }

    public static class HorarioRequest {
        public UUID idUsuario;
        public String nombre;
        public LocalTime horaEntrada;
        public LocalTime horaSalida;
        public String diasLaborables; // e.g. "lunes,martes,miercoles"
        public LocalDate fechaInicio;
        public LocalDate fechaFin;
    }
}
