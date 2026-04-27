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
import org.springframework.web.multipart.MultipartFile;


import com.example.proyectoFichaje.models.Solicitudes;
import com.example.proyectoFichaje.service.SolicitudService;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/solicitudes")
@CrossOrigin(origins = "*")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    @GetMapping
    public ResponseEntity<?> obtenerTodas() {
        try {
            return ResponseEntity.ok(solicitudService.obtenerTodas());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error capturado: " + e.getMessage());
        }
    }

    @GetMapping("/test-simple")
    public String testSimple() {
        return "OK";
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

    @PostMapping("/ausencia")
    public ResponseEntity<?> crearSolicitudAusencia(
            @RequestParam UUID idUsuario,
            @RequestParam String motivo,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam(required = false) String comentario,
            @RequestParam(required = false) MultipartFile archivo) {
        try {
            Solicitudes solicitud = solicitudService.crearSolicitudAusencia(
                idUsuario, motivo, fechaInicio, fechaFin, comentario, archivo
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

    @GetMapping("/archivo/{filename}")
    public ResponseEntity<Resource> getArchivo(@PathVariable String filename) {
        try {
            Path path = Paths.get("uploads/justificantes").resolve(filename);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
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
