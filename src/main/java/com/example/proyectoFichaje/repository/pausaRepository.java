package com.example.proyectoFichaje.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyectoFichaje.models.Pausas;

@Repository
public interface pausaRepository extends JpaRepository<Pausas, UUID> {

    List<Pausas> findByFichaje_IdFichajes(UUID idFichajes);
    Optional<Pausas> findByFichaje_IdFichajesAndFinIsNull(UUID idFichajes);
}
