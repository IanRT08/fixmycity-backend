package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.reporteAsignadoCuadrilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface reporteAsignadoCuadrillaRepository extends JpaRepository<reporteAsignadoCuadrilla, Integer> {

    //Modulo 3.2 - Obtener la asignacion de un reporte especifico
    @Query(value = "SELECT rac.idReporte, rac.idCuadrilla, rac.fechaAsignacion " +
            "FROM reporteAsignadoCuadrilla rac " +
            "INNER JOIN cuadrilla c ON rac.idCuadrilla = c.idCuadrilla " +
            "WHERE rac.idReporte = :idReporte",
            nativeQuery = true)
    Optional<reporteAsignadoCuadrilla> findByReporte(@Param("idReporte") int idReporte);

    //Modulo 3.3 - Listar todos los reportes asignados a una cuadrilla
    @Query(value = "SELECT rac.idReporte, rac.idCuadrilla, rac.fechaAsignacion " +
            "FROM reporteAsignadoCuadrilla rac " +
            "INNER JOIN detallesReporte dr ON rac.idReporte = dr.idReporte " +
            "WHERE rac.idCuadrilla = :idCuadrilla " +
            "AND dr.estado IN ('Asignado', 'En camino', 'En curso')",
            nativeQuery = true)
    List<reporteAsignadoCuadrilla> findActiveByCuadrilla(@Param("idCuadrilla") int idCuadrilla);

    /** Elimina la asignación previa de un reporte (usado al reasignar). */
    @Modifying
    @Transactional
    @Query("DELETE FROM reporteAsignadoCuadrilla r WHERE r.idReporte.idReporte = :idReporte")
    void deleteByReporte(@Param("idReporte") int idReporte);
}