package com.example.proyectoFichaje.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "solicitudes")
public class Solicitudes {

    @Id
    @JdbcTypeCode(java.sql.Types.CHAR)
    @Column(name = "id_solicitud", length = 36, columnDefinition = "CHAR(36)")
    private UUID idSolicitud;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuarios usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_revisor")
    private Usuarios revisor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private Tipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private Estado estado = Estado.pendiente;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    // Obligatorio si tipo = correccion_fichaje (validado en el servicio)
    @Enumerated(EnumType.STRING)
    @Column(name = "motivo")
    private Motivo motivo;

    // Obligatorio si tipo = correccion_fichaje (validado en el servicio)
    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_fichaje_ref")
    private Fichajes fichajeRef;

    @Column(name = "fecha_revision")
    private LocalDateTime fechaRevision;

    @Column(name = "hora_entrada_propuesta")
    private LocalTime horaEntradaPropuesta;

    @Column(name = "hora_salida_propuesta")
    private LocalTime horaSalidaPropuesta;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Tipo {
        vacaciones, permiso_horas, baja_medica, correccion_fichaje
    }

    public enum Estado {
        pendiente, aprobada, rechazada
    }

    public enum Motivo {
        error_entrada, error_salida, olvido_fichaje, problema_tecnico, otro
    }

    @PrePersist
    public void prePersist() {
        if (idSolicitud == null) {
            idSolicitud = UUID.randomUUID();
        }
    }

    public UUID getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(UUID idSolicitud) { this.idSolicitud = idSolicitud; }
    public Usuarios getUsuario() { return usuario; }
    public void setUsuario(Usuarios usuario) { this.usuario = usuario; }
    public Usuarios getRevisor() { return revisor; }
    public void setRevisor(Usuarios revisor) { this.revisor = revisor; }
    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public Motivo getMotivo() { return motivo; }
    public void setMotivo(Motivo motivo) { this.motivo = motivo; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public Fichajes getFichajeRef() { return fichajeRef; }
    public void setFichajeRef(Fichajes fichajeRef) { this.fichajeRef = fichajeRef; }
    public LocalTime getHoraEntradaPropuesta() { return horaEntradaPropuesta; }
    public void setHoraEntradaPropuesta(LocalTime horaEntradaPropuesta) { this.horaEntradaPropuesta = horaEntradaPropuesta; }
    public LocalTime getHoraSalidaPropuesta() { return horaSalidaPropuesta; }
    public void setHoraSalidaPropuesta(LocalTime horaSalidaPropuesta) { this.horaSalidaPropuesta = horaSalidaPropuesta; }
    public LocalDateTime getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(LocalDateTime fechaRevision) { this.fechaRevision = fechaRevision; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
