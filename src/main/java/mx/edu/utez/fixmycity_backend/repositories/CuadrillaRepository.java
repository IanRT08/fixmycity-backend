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
    @Query(value = "SELECT c.idCuadrilla " +
            "FROM cuadrilla c " +
            "WHERE c.nombreCuadrilla = :nombre",
            nativeQuery = true)
    Optional<Object> findIdByNombreCuadrilla(@Param("nombre") String nombre);

    //Modulo 3.2 - Listar cuadrillas activas de un municipio para asignar un reporte
    @Query(value = "SELECT c.idCuadrilla " +
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
    List<Object> findAvailableIdsByMunicipio(@Param("idMunicipio") int idMunicipio);

    //Modulo 3.1 - Listar todas las cuadrillas sin filtro de municipio (superadmin)
    @Query(value = "SELECT c.idCuadrilla " +
            "FROM cuadrilla c " +
            "WHERE c.estado = :estado",
            nativeQuery = true)
    List<Object> findIdsByEstado(@Param("estado") String estado);

    //Modulo 3.1 - Listar todas las cuadrillas para el panel de administracion filtradas por municipio
    @Query(value = "SELECT c.idCuadrilla " +
            "FROM cuadrilla c " +
            "WHERE c.estado = :estado " +
            "AND c.idMunicipio = :idMunicipio",
            nativeQuery = true)
    List<Object> findIdsByEstadoAndMunicipio(@Param("estado") String estado, @Param("idMunicipio") int idMunicipio);

    //Dashboard - Conteo de cuadrillas libres (activas sin reporte en proceso) por municipio
    @Query(value = "SELECT COUNT(c.idCuadrilla) " +
            "FROM cuadrilla c " +
            "WHERE c.estado = 'activa' " +
            "AND (:idMunicipio IS NULL OR c.idMunicipio = :idMunicipio) " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM reporteAsignadoCuadrilla rac " +
            "   INNER JOIN detallesReporte dr ON rac.idReporte = dr.idReporte " +
            "   WHERE rac.idCuadrilla = c.idCuadrilla " +
            "   AND dr.estado IN ('Asignado', 'En camino', 'En curso'))",
            nativeQuery = true)
    int countCuadrillasLibres(@Param("idMunicipio") Integer idMunicipio);

    //Dashboard - Conteo de cuadrillas asignadas (con reporte en proceso) por municipio
    @Query(value = "SELECT COUNT(DISTINCT c.idCuadrilla) " +
            "FROM cuadrilla c " +
            "INNER JOIN reporteAsignadoCuadrilla rac ON c.idCuadrilla = rac.idCuadrilla " +
            "INNER JOIN detallesReporte dr ON rac.idReporte = dr.idReporte " +
            "WHERE c.estado = 'activa' " +
            "AND (:idMunicipio IS NULL OR c.idMunicipio = :idMunicipio) " +
            "AND dr.estado IN ('Asignado', 'En camino', 'En curso')",
            nativeQuery = true)
    int countCuadrillasAsignadas(@Param("idMunicipio") Integer idMunicipio);

    //Dashboard - Conteo total de cuadrillas activas por municipio
    @Query(value = "SELECT COUNT(c.idCuadrilla) " +
            "FROM cuadrilla c " +
            "WHERE c.estado = 'activa' " +
            "AND (:idMunicipio IS NULL OR c.idMunicipio = :idMunicipio)",
            nativeQuery = true)
    int countCuadrillasActivas(@Param("idMunicipio") Integer idMunicipio);

//Modulo 3.1 - Cambiar el estado de una cuadrilla
    @Modifying
    @Transactional
    @Query(value = "UPDATE cuadrilla SET estado = :estado " +
            "WHERE idCuadrilla = :id",
            nativeQuery = true)
    void updateEstado(@Param("id") int id, @Param("estado") String estado);
}