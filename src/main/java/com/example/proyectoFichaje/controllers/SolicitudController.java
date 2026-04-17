package com.example.proyectoFichaje.controllers;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyectoFichaje.models.Solicitudes;
import com.example.proyectoFichaje.service.SolicitudService;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping
    public List<Solicitudes> obtenerTodas() {
        return solicitudService.obtenerTodas();
    }

    @GetMapping("/pendientes")
    public List<Solicitudes> obtenerPendientes() {
        return solicitudService.obtenerPendientes();
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Solicitudes> obtenerPorUsuario(@PathVariable UUID idUsuario) {
        return solicitudService.obtenerPorUsuario(idUsuario);
    }

    @PostMapping("/correccion")
    public ResponseEntity<?> crearSolicitud(@RequestBody CorreccionRequest request) {
        try {
            Solicitudes solicitud = solicitudService.crearSolicitudCorreccion(
                request.idUsuario,
                request.idFichaje,
                request.nuevaHoraEntrada,
                request.nuevaHoraSalida,
                request.comentario
            );
            return ResponseEntity.ok(solicitud);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/resolver")
    public ResponseEntity<?> resolverSolicitud(
            @PathVariable UUID id,
            @RequestParam boolean aprobado,
            @RequestParam UUID idRevisor) {
        try {
            Solicitudes solicitud = solicitudService.resolverSolicitud(id, aprobado, idRevisor);
            return ResponseEntity.ok(solicitud);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    static class CorreccionRequest {
        public UUID idUsuario;
        public UUID idFichaje;
        public LocalTime nuevaHoraEntrada;
        public LocalTime nuevaHoraSalida;
        public String comentario;
    }
}
