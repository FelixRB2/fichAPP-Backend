package com.example.proyectoFichaje.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

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

        fichaje.setHoraSalida(LocalTime.now());
        fichaje.setEstado(Fichajes.EstadoFichaje.normal);
        return fichajeRepo.save(fichaje);
    }
}
