package com.example.proyectoFichaje.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyectoFichaje.models.Rol;

@Repository
public interface rolRepository extends JpaRepository<Rol, UUID>{

    Optional<Rol> findByNombreRol(Rol.NombreRol nombreRol);
    
}
