package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.Votacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface VotacionRepository extends JpaRepository<Votacion, Integer> {

    //Modulo 3.3 - Verificar si un voluntario ya voto en un reporte
    @Query(value = "SELECT v.idVoto, v.idReporte, v.idCuadrilla, v.idVoluntario, " +
            "v.respuesta, v.fechaVoto " +
            "FROM votacion v " +
            "WHERE v.idReporte = :idReporte " +
            "AND v.idVoluntario = :idVoluntario",
            nativeQuery = true)
    Optional<Votacion> findByReporteAndVoluntario(
            @Param("idReporte") int idReporte,
            @Param("idVoluntario") int idVoluntario);

    //Modulo 3.3 - Contar votos de aceptacion para verificar si se alcanzo mayoria
    @Query(value = "SELECT COUNT(v.idVoto) " +
            "FROM votacion v " +
            "INNER JOIN miembrosCuadrilla mc ON v.idVoluntario = mc.idMiembro " +
            "WHERE v.idReporte = :idReporte " +
            "AND v.idCuadrilla = :idCuadrilla " +
            "AND v.respuesta = 'aceptar'",
            nativeQuery = true)
    int countAcceptedVotes(
            @Param("idReporte") int idReporte,
            @Param("idCuadrilla") int idCuadrilla);

    //Modulo 3.3 - Verificar si el lider de la cuadrilla ya voto y cual fue su respuesta
    @Query(value = "SELECT v.idVoto, v.idReporte, v.idCuadrilla, v.idVoluntario, " +
            "v.respuesta, v.fechaVoto " +
            "FROM votacion v " +
            "INNER JOIN cuadrilla c ON v.idCuadrilla = c.idCuadrilla " +
            "WHERE v.idReporte = :idReporte " +
            "AND v.idVoluntario = c.idLider",
            nativeQuery = true)
    Optional<Votacion> findLiderVoteByReporte(@Param("idReporte") int idReporte);

    @Modifying
    @Transactional
    @Query("DELETE FROM Votacion v WHERE v.idVoluntario.idVoluntario = :idVoluntario AND v.idCuadrilla.idCuadrilla = :idCuadrilla")
    void deleteByVoluntarioAndCuadrilla(@Param("idVoluntario") int idVoluntario, @Param("idCuadrilla") int idCuadrilla);

    @Modifying
    @Transactional
    @Query("DELETE FROM Votacion v WHERE v.idVoluntario.idVoluntario = :idVoluntario")
    void deleteByVoluntario(@Param("idVoluntario") int idVoluntario);

    /** Elimina todos los votos de un reporte al reasignarlo, para permitir nueva votación. */
    @Modifying
    @Transactional
    @Query("DELETE FROM Votacion v WHERE v.idReporte.idReporte = :idReporte")
    void deleteByReporte(@Param("idReporte") int idReporte);
}