package com.example.proyectoFichaje.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyectoFichaje.models.Rol.NombreRol;
import com.example.proyectoFichaje.models.Usuarios;

@Repository
public interface usuariosRepository extends JpaRepository<Usuarios, UUID> {

    Optional<Usuarios> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    List<Usuarios> findByEstado(Usuarios.EstadoUsuario estado);
    List<Usuarios> findByRol_NombreRol(NombreRol nombreRol);
}
