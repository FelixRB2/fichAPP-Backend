package com.example.proyectoFichaje.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyectoFichaje.models.Solicitudes;

@Repository
public interface solicitudesRepository extends JpaRepository<Solicitudes, UUID> {

    List<Solicitudes> findByUsuario_IdUsuarioOrderByCreatedAtDesc(UUID idUsuario);
    List<Solicitudes> findByEstadoOrderByCreatedAtDesc(Solicitudes.Estado estado);
    List<Solicitudes> findByUsuario_IdUsuarioAndEstado(UUID idUsuario, Solicitudes.Estado estado);
    List<Solicitudes> findByMotivo(Solicitudes.Motivo motivo);
}
