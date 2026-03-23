package com.example.proyectoFichaje.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.proyectoFichaje.models.Horarios;

@Repository
public interface horariosRepository extends JpaRepository<Horarios, UUID> {

    List<Horarios> findByUsuario_IdUsuario(UUID idUsuario);

    // Horario vigente: fecha_fin null (indefinido) o aún no ha expirado
    @Query("SELECT h FROM Horarios h WHERE h.usuario.idUsuario = :idUsuario AND (h.fechaFin IS NULL OR h.fechaFin >= :hoy)")
    Optional<Horarios> findHorarioVigente(@Param("idUsuario") UUID idUsuario, @Param("hoy") LocalDate hoy);

    // Horarios que incluyan un día concreto (búsqueda en el SET de MySQL)
    @Query(value = "SELECT * FROM horarios WHERE id_usuario = :idUsuario AND FIND_IN_SET(:dia, dias_laborables) > 0", nativeQuery = true)
    List<Horarios> findByUsuarioAndDia(@Param("idUsuario") UUID idUsuario, @Param("dia") String dia);
}
