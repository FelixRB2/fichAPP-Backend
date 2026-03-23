package com.example.proyectoFichaje.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyectoFichaje.models.Fichajes;

@Repository
public interface fichajeRepository extends JpaRepository<Fichajes, UUID> {

    List<Fichajes> findByUsuario_IdUsuarioOrderByFechaDesc(UUID idUsuario);
    List<Fichajes> findByUsuario_IdUsuarioAndFechaBetweenOrderByFechaDesc(UUID idUsuario, LocalDate desde, LocalDate hasta);
    Optional<Fichajes> findByUsuario_IdUsuarioAndFechaAndHoraSalidaIsNull(UUID idUsuario, LocalDate fecha);
    List<Fichajes> findByEstado(Fichajes.EstadoFichaje estado);
    List<Fichajes> findByComentarioIsNotNullAndEstado(Fichajes.EstadoFichaje estado);
}
