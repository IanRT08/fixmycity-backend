package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.Cuadrilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuadrillaRepository extends JpaRepository<Cuadrilla, Integer> {

    //Modulo 3.1 - Verificar si ya existe una cuadrilla con ese nombre
    @Query(value = "SELECT c.idCuadrilla, c.nombreCuadrilla, c.idMunicipio, c.idLider, c.estado " +
            "FROM cuadrilla c " +
            "WHERE c.nombreCuadrilla = :nombre",
            nativeQuery = true)
    Optional<Cuadrilla> findByNombreCuadrilla(@Param("nombre") String nombre);

    //Modulo 3.2 - Listar cuadrillas activas de un municipio para asignar un reporte
    @Query(value = "SELECT c.idCuadrilla, c.nombreCuadrilla, c.idMunicipio, c.idLider, c.estado " +
            "FROM cuadrilla c " +
            "INNER JOIN municipios m ON c.idMunicipio = m.idMunicipio " +
            "WHERE c.idMunicipio = :idMunicipio " +
            "AND c.estado = 'activa' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM reporteAsignadoCuadrilla rac " +
            "   INNER JOIN detallesReporte dr ON rac.idReporte = dr.idReporte " +
            "   WHERE rac.idCuadrilla = c.idCuadrilla " +
            "   AND dr.estado IN ('Asignado', 'En camino', 'En curso'))",
            nativeQuery = true)
    List<Cuadrilla> findAvailableByMunicipio(@Param("idMunicipio") int idMunicipio);

    //Modulo 3.1 - Listar todas las cuadrillas para el panel de administracion
    @Query(value = "SELECT c.idCuadrilla, c.nombreCuadrilla, c.idMunicipio, c.idLider, c.estado " +
            "FROM cuadrilla c " +
            "INNER JOIN municipios m ON c.idMunicipio = m.idMunicipio " +
            "WHERE c.estado = :estado",
            nativeQuery = true)
    List<Cuadrilla> findByEstado(@Param("estado") String estado);

    //Modulo 3.1 - Cambiar el estado de una cuadrilla
    @Modifying
    @Transactional
    @Query(value = "UPDATE cuadrilla SET estado = :estado " +
            "WHERE idCuadrilla = :id",
            nativeQuery = true)
    void updateEstado(@Param("id") int id, @Param("estado") String estado);
}