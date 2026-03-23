package com.example.proyectoFichaje.models;

import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "Rol")
public class Rol {
    
    @Id
    @JdbcTypeCode(java.sql.Types.CHAR)
    @Column(name = "id_rol", length = 36, columnDefinition = "CHAR(36)")
    private UUID idRol;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre_rol", nullable = false)
    private NombreRol nombreRol;

    public enum NombreRol {
        Trabajador, Administrador
    }

    @PrePersist
    public void prePersist() {
        if (idRol == null) {
            idRol = UUID.randomUUID();
        }
    }

    public UUID getIdRol() { return idRol; }
    public void setIdRol(UUID idRol) { this.idRol = idRol; }

    public NombreRol getNombreRol() { return nombreRol; }
    public void setNombreRol(NombreRol nombreRol) { this.nombreRol = nombreRol; }

}
