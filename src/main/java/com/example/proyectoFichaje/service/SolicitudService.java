package com.example.proyectoFichaje.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyectoFichaje.models.Fichajes;
import com.example.proyectoFichaje.models.Solicitudes;
import com.example.proyectoFichaje.models.Usuarios;
import com.example.proyectoFichaje.repository.fichajeRepository;
import com.example.proyectoFichaje.repository.solicitudesRepository;
import com.example.proyectoFichaje.repository.usuariosRepository;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Service
public class SolicitudService {

    private final solicitudesRepository solicitudRepo;
    private final fichajeRepository fichajeRepo;
    private final usuariosRepository usuarioRepo;
    private final String uploadDir = "uploads/justificantes";

    public SolicitudService(solicitudesRepository solicitudRepo, fichajeRepository fichajeRepo, usuariosRepository usuarioRepo) {
        this.solicitudRepo = solicitudRepo;
        this.fichajeRepo = fichajeRepo;
        this.usuarioRepo = usuarioRepo;
        
        // Crear directorio de subidas si no existe
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (Exception e) {
            System.err.println("No se pudo crear el directorio de subidas: " + e.getMessage());
        }
    }

    public List<Solicitudes> obtenerTodas() {
        return solicitudRepo.findAll();
    }

    public List<Solicitudes> obtenerPendientes() {
        return solicitudRepo.findByEstadoOrderByCreatedAtDesc(Solicitudes.Estado.pendiente);
    }

    public List<Solicitudes> obtenerPorUsuario(UUID idUsuario) {
        return solicitudRepo.findByUsuario_IdUsuarioOrderByCreatedAtDesc(idUsuario);
    }

    @Transactional
    public Solicitudes crearSolicitudCorreccion(UUID idUsuario, UUID idFichaje, LocalTime nuevaEntrada, LocalTime nuevaSalida, String comentario) {
        Usuarios usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Fichajes fichaje = fichajeRepo.findById(idFichaje)
                .orElseThrow(() -> new RuntimeException("Fichaje no encontrado"));

        if (comentario == null || comentario.isBlank()) {
            throw new RuntimeException("El comentario es obligatorio para corregir un fichaje");
        }

        Solicitudes solicitud = new Solicitudes();
        solicitud.setUsuario(usuario);
        solicitud.setFichajeRef(fichaje);
        solicitud.setEstado(Solicitudes.Estado.pendiente);
        solicitud.setFechaInicio(fichaje.getFecha());
        solicitud.setFechaFin(fichaje.getFecha());
        solicitud.setHoraEntradaPropuesta(nuevaEntrada);
        solicitud.setHoraSalidaPropuesta(nuevaSalida);
        solicitud.setComentario(comentario);
        solicitud.setMotivo(Solicitudes.Motivo.correccion_fichaje);

        return solicitudRepo.save(solicitud);
    }

    @Transactional
    public Solicitudes crearSolicitudAusencia(UUID idUsuario, String motivo, String fechaInicio, String fechaFin, String comentario, MultipartFile archivo) {
        Usuarios usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Solicitudes solicitud = new Solicitudes();
        solicitud.setUsuario(usuario);
        solicitud.setMotivo(Solicitudes.Motivo.valueOf(motivo));
        solicitud.setEstado(Solicitudes.Estado.pendiente);
        solicitud.setFechaInicio(LocalDate.parse(fechaInicio));
        solicitud.setFechaFin(LocalDate.parse(fechaFin));
        solicitud.setComentario(comentario);

        if (archivo != null && !archivo.isEmpty()) {
            try {
                String originalName = archivo.getOriginalFilename();
                String extension = originalName.substring(originalName.lastIndexOf("."));
                String nuevoNombre = UUID.randomUUID().toString() + extension;
                Path path = Paths.get(uploadDir, nuevoNombre);
                Files.copy(archivo.getInputStream(), path);
                
                solicitud.setArchivoNombre(originalName);
                // La URL será para un endpoint que servirá el archivo
                solicitud.setArchivoUrl("http://localhost:8085/api/solicitudes/archivo/" + nuevoNombre);
            } catch (Exception e) {
                throw new RuntimeException("Error al guardar el archivo: " + e.getMessage());
            }
        }

        return solicitudRepo.save(solicitud);
    }

    @Transactional
    public Solicitudes resolverSolicitud(UUID idSolicitud, boolean aprobado, UUID idRevisor) {
        Solicitudes solicitud = solicitudRepo.findById(idSolicitud)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        Usuarios revisor = usuarioRepo.findById(idRevisor)
                .orElseThrow(() -> new RuntimeException("Revisor no encontrado"));

        solicitud.setEstado(aprobado ? Solicitudes.Estado.aprobada : Solicitudes.Estado.rechazada);
        solicitud.setRevisor(revisor);
        solicitud.setFechaRevision(LocalDateTime.now());

        if (aprobado && solicitud.getMotivo() == Solicitudes.Motivo.correccion_fichaje && solicitud.getFichajeRef() != null) {
            Fichajes fichaje = solicitud.getFichajeRef();
            fichaje.setHoraEntrada(solicitud.getHoraEntradaPropuesta());
            fichaje.setHoraSalida(solicitud.getHoraSalidaPropuesta());
            fichaje.setEstado(Fichajes.EstadoFichaje.editado);
            
            // Recalcular horas trabajadas
            if (fichaje.getHoraEntrada() != null && fichaje.getHoraSalida() != null) {
                Duration duration = Duration.between(fichaje.getHoraEntrada(), fichaje.getHoraSalida());
                double hours = duration.toMinutes() / 60.0;
                fichaje.setHorasTrabajadas(BigDecimal.valueOf(hours).setScale(2, RoundingMode.HALF_UP));
            }
            
            fichajeRepo.save(fichaje);
        }

        return solicitudRepo.save(solicitud);
    }
}
