package com.example.proyectoFichaje.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.example.proyectoFichaje.models.Fichajes;
import com.example.proyectoFichaje.models.Usuarios;
import com.example.proyectoFichaje.repository.fichajeRepository;
import com.example.proyectoFichaje.repository.usuariosRepository;

@Service
public class FichajeService {

    private final fichajeRepository fichajeRepo;
    private final usuariosRepository usuarioRepo;

    public FichajeService(fichajeRepository fichajeRepo, usuariosRepository usuarioRepo) {
        this.fichajeRepo = fichajeRepo;
        this.usuarioRepo = usuarioRepo;
    }

    // CREAR fichaje (entrada)
    public Fichajes crear(UUID idUsuario, LocalTime horaEntrada, LocalTime horaSalida, LocalDate fecha, String comentario) {
        Usuarios usuario = usuarioRepo.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idUsuario));

        // Comprobar que no hay ya un fichaje abierto hoy
        fichajeRepo.findByUsuario_IdUsuarioAndFechaAndHoraSalidaIsNull(idUsuario, fecha)
            .ifPresent(f -> { throw new RuntimeException("Ya existe un fichaje abierto para hoy."); });

        Fichajes fichaje = new Fichajes();
        fichaje.setUsuario(usuario);
        fichaje.setFecha(LocalDate.now());
        fichaje.setHoraEntrada(LocalTime.now());
        fichaje.setHoraSalida(LocalTime.now());
        fichaje.setComentario(comentario);
        fichaje.setEstado(Fichajes.EstadoFichaje.normal);
        return fichajeRepo.save(fichaje);
    }

    // UPDATE fichaje: modificar horas (comentario obligatorio)
    public Fichajes actualizar(UUID idFichaje, LocalTime horaEntrada, LocalTime horaSalida, String comentario) {
        if (comentario == null || comentario.isBlank()) {
            throw new RuntimeException("El comentario es obligatorio al modificar un fichaje.");
        }

        Fichajes fichaje = fichajeRepo.findById(idFichaje)
            .orElseThrow(() -> new RuntimeException("Fichaje no encontrado: " + idFichaje));

        fichaje.setHoraEntrada(horaEntrada);
        fichaje.setHoraSalida(horaSalida);
        fichaje.setComentario(comentario);
        fichaje.setEstado(Fichajes.EstadoFichaje.editado);
        return fichajeRepo.save(fichaje);
    }

    // READ: obtener todos los fichajes de un usuario
    public List<Fichajes> obtenerPorUsuario(UUID idUsuario) {
        return fichajeRepo.findByUsuario_IdUsuarioOrderByFechaDesc(idUsuario);
    }

    public Fichajes registrarEntrada(UUID idUsuario, String comentario) {
        Usuarios usuario = usuarioRepo.findById(idUsuario)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idUsuario));

        fichajeRepo.findByUsuario_IdUsuarioAndFechaAndHoraSalidaIsNull(idUsuario, LocalDate.now())
            .ifPresent(f -> { throw new RuntimeException("Ya existe un fichaje abierto hoy."); });

        Fichajes fichaje = new Fichajes();
        fichaje.setUsuario(usuario);
        fichaje.setFecha(LocalDate.now());
        fichaje.setHoraEntrada(LocalTime.now());
        fichaje.setHoraSalida(null);
        fichaje.setComentario(comentario);
        fichaje.setEstado(Fichajes.EstadoFichaje.normal);
        return fichajeRepo.save(fichaje);
    }

    // PUT /{id}/salida: registrar salida automática
    public Fichajes registrarSalida(UUID idFichaje) {
        Fichajes fichaje = fichajeRepo.findById(idFichaje)
            .orElseThrow(() -> new RuntimeException("Fichaje no encontrado: " + idFichaje));

        if (fichaje.getHoraSalida() != null) {
            throw new RuntimeException("Este fichaje ya tiene hora de salida registrada.");
        }

        LocalTime salida = LocalTime.now();
        fichaje.setHoraSalida(salida);
        fichaje.setEstado(Fichajes.EstadoFichaje.normal);

        // Calculate hours worked
        Duration duration = Duration.between(fichaje.getHoraEntrada(), salida);
        double hours = duration.toMinutes() / 60.0;
        fichaje.setHorasTrabajadas(BigDecimal.valueOf(hours).setScale(2, RoundingMode.HALF_UP));

        return fichajeRepo.save(fichaje);
    }

    public Usuarios getDashboardData(UUID idUsuario) {
        Usuarios usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + idUsuario));

        List<Fichajes> history = fichajeRepo.findByUsuario_IdUsuarioOrderByFechaDesc(idUsuario);
        usuario.setRecentHistory(history);

        // Active fichaje
        usuario.setActiveFichaje(history.stream()
                .filter(f -> f.getHoraSalida() == null)
                .findFirst().orElse(null));

        // Weekly Summary (7 days)
        LocalDate oneWeekAgo = LocalDate.now().minusDays(7);
        long totalMinutes = history.stream()
                .filter(f -> !f.getFecha().isBefore(oneWeekAgo) && f.getHoraSalida() != null)
                .mapToLong(f -> Duration.between(f.getHoraEntrada(), f.getHoraSalida()).toMinutes())
                .sum();

        long h = totalMinutes / 60;
        long m = totalMinutes % 60;
        usuario.setWeeklyHours(String.format("%dh %02dm", h, m));

        // Assuming 40h target
        usuario.setWeeklyPercentage(Math.min(100.0, ((double)totalMinutes / (40.0 * 60.0)) * 100.0));

        return usuario;
    }

    // --- NUEVOS MÉTODOS PARA CORRECCIÓN DE FICHAJES (OPCIÓN A) ---

    @org.springframework.transaction.annotation.Transactional
    public Fichajes solicitarCorreccion(UUID idFichaje, LocalTime entrada, LocalTime salida, String comentario) {
        Fichajes fichaje = fichajeRepo.findById(idFichaje)
                .orElseThrow(() -> new RuntimeException("Fichaje no encontrado"));

        if (comentario == null || comentario.isBlank()) {
            throw new RuntimeException("El comentario es obligatorio para solicitar una corrección.");
        }

        fichaje.setHoraEntradaPropuesta(entrada);
        fichaje.setHoraSalidaPropuesta(salida);
        fichaje.setComentario(comentario);
        fichaje.setEstado(Fichajes.EstadoFichaje.pendiente_revision);

        return fichajeRepo.save(fichaje);
    }

    @org.springframework.transaction.annotation.Transactional
    public Fichajes resolverCorreccion(UUID idFichaje, boolean aprobado) {
        Fichajes fichaje = fichajeRepo.findById(idFichaje)
                .orElseThrow(() -> new RuntimeException("Fichaje no encontrado"));

        if (aprobado) {
            fichaje.setHoraEntrada(fichaje.getHoraEntradaPropuesta());
            fichaje.setHoraSalida(fichaje.getHoraSalidaPropuesta());
            fichaje.setEstado(Fichajes.EstadoFichaje.editado);
            
            // Recalcular horas trabajadas
            if (fichaje.getHoraEntrada() != null && fichaje.getHoraSalida() != null) {
                Duration duration = Duration.between(fichaje.getHoraEntrada(), fichaje.getHoraSalida());
                double hours = duration.toMinutes() / 60.0;
                fichaje.setHorasTrabajadas(BigDecimal.valueOf(hours).setScale(2, RoundingMode.HALF_UP));
            }
        } else {
            // Si se rechaza, vuelve a estado normal (o el que tuviera) y se limpian las propuestas
            fichaje.setEstado(Fichajes.EstadoFichaje.normal);
        }

        // Limpiar campos de propuesta tras resolver
        fichaje.setHoraEntradaPropuesta(null);
        fichaje.setHoraSalidaPropuesta(null);

        return fichajeRepo.save(fichaje);
    }

    public List<Fichajes> obtenerPendientesRevision() {
        return fichajeRepo.findByEstado(Fichajes.EstadoFichaje.pendiente_revision);
    }
}
