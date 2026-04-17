package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.reporteFinalizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface reporteFinalizadoRepository extends JpaRepository<reporteFinalizado, Integer> {

    //Modulo 3.6 - Obtener la evidencia y comentarios de un reporte finalizado
    @Query(value = "SELECT rf.idReporte, rf.fotoEvidencia, rf.comentarios, " +
            "rf.idCuadrillaEncargada, rf.fechaFinalizacion " +
            "FROM reporteFinalizado rf " +
            "INNER JOIN cuadrilla c ON rf.idCuadrillaEncargada = c.idCuadrilla " +
            "WHERE rf.idReporte = :idReporte",
            nativeQuery = true)
    Optional<reporteFinalizado> findByReporte(@Param("idReporte") int idReporte);

    // Obtener el último reporte finalizado de una cuadrilla
    @Query(value = "SELECT rf.idReporte, rf.fotoEvidencia, rf.comentarios, " +
            "rf.idCuadrillaEncargada, rf.fechaFinalizacion " +
            "FROM reporteFinalizado rf " +
            "WHERE rf.idCuadrillaEncargada = :idCuadrilla " +
            "ORDER BY rf.fechaFinalizacion DESC FETCH FIRST 1 ROWS ONLY",
            nativeQuery = true)
    Optional<reporteFinalizado> findLastByCuadrilla(@Param("idCuadrilla") int idCuadrilla);
}
