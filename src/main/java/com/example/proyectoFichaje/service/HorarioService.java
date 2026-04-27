package com.example.proyectoFichaje.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.proyectoFichaje.models.Horarios;
import com.example.proyectoFichaje.models.Usuarios;
import com.example.proyectoFichaje.repository.horariosRepository;
import com.example.proyectoFichaje.repository.usuariosRepository;

@Service
public class HorarioService {

    private final horariosRepository horarioRepo;
    private final usuariosRepository usuarioRepo;

    public HorarioService(horariosRepository horarioRepo, usuariosRepository usuarioRepo) {
        this.horarioRepo = horarioRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @Transactional
    public Horarios asignarHorario(UUID idUsuario, String nombre, LocalTime entrada, LocalTime salida, String dias, LocalDate inicio, LocalDate fin) {
        Usuarios usuario = usuarioRepo.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Horarios horario = new Horarios();
        horario.setUsuario(usuario);
        horario.setNombre(nombre);
        horario.setHoraEntrada(entrada);
        horario.setHoraSalida(salida);
        horario.setDiasLaborables(dias);
        horario.setFechaInicio(inicio);
        horario.setFechaFin(fin);

        return horarioRepo.save(horario);
    }

    public List<Horarios> listarPorUsuario(UUID idUsuario) {
        return horarioRepo.findByUsuario_IdUsuario(idUsuario);
    }

    public List<Horarios> obtenerHorariosVigentes(UUID idUsuario) {
        return horarioRepo.findHorariosVigentes(idUsuario, LocalDate.now());
    }

    @Transactional
    public void eliminarHorario(UUID idHorario) {
        horarioRepo.deleteById(idHorario);
    }
}
