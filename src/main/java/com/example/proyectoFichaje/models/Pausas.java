package com.example.proyectoFichaje.models;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "pausas")
public class Pausas {

    @Id
    @JdbcTypeCode(java.sql.Types.CHAR)
    @Column(name = "id_pausas", length = 36)
    private UUID idPausas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_fichajes", nullable = false)
    private Fichajes fichaje;

    @Column(name = "inicio", nullable = false)
    private LocalDateTime inicio;

    @Column(name = "fin")
    private LocalDateTime fin;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (idPausas == null) {
            idPausas = UUID.randomUUID();
        }
    }
    
}
