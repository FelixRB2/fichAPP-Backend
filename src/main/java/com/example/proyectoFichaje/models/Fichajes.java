package com.example.proyectoFichaje.models;

import java.math.BigDecimal;
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
@Table(name = "fichajes")
public class Fichajes {

    @Id
    @JdbcTypeCode(java.sql.Types.CHAR)
    @Column(name = "id_fichajes", columnDefinition = "CHAR(36)")
    private UUID idFichajes;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuarios usuario;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_entrada", nullable = false)
    private LocalTime horaEntrada; //= LocalTime.now();

    @Column(name = "hora_salida")
    private LocalTime horaSalida; //= LocalTime.now();

    @Column(name = "minutos_descanso")
    private Integer minutosDescanso = 0;

    @Column(name = "horas_trabajadas", precision = 5, scale = 2)
    private BigDecimal horasTrabajadas;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoFichaje estado = EstadoFichaje.normal;

    // Opcional al fichar, obligatorio al editar (validado en el servicio)
    @Column(name = "comentario", columnDefinition = "TEXT")
    private String comentario;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum EstadoFichaje {
        normal, editado, pendiente_revision
    }

    @PrePersist
    public void prePersist() {
        if (idFichajes == null) {
            idFichajes = UUID.randomUUID();
        }
    }

    public UUID getIdFichajes() { return idFichajes; }
    public void setIdFichajes(UUID idFichajes) { this.idFichajes = idFichajes; }
    public Usuarios getUsuario() { return usuario; }
    public void setUsuario(Usuarios usuario) { this.usuario = usuario; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public LocalTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalTime horaEntrada) { this.horaEntrada = horaEntrada; }
    public LocalTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalTime horaSalida) { this.horaSalida = horaSalida; }
    public Integer getMinutosDescanso() { return minutosDescanso; }
    public void setMinutosDescanso(Integer minutosDescanso) { this.minutosDescanso = minutosDescanso; }
    public BigDecimal getHorasTrabajadas() { return horasTrabajadas; }
    public void setHorasTrabajadas(BigDecimal horasTrabajadas) { this.horasTrabajadas = horasTrabajadas; }
    public EstadoFichaje getEstado() { return estado; }
    public void setEstado(EstadoFichaje estado) { this.estado = estado; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
