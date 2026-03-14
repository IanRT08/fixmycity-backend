package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {

    //Modulo 2.2 - Consultar todos los reportes del ciudadano autenticado
    @Query(value = "SELECT r.idReporte " +
            "FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE r.idUsuario = :idUsuario",
            nativeQuery = true)
    List<Object> findIdsByUsuario(@Param("idUsuario") int idUsuario);

    //Modulo 2.2 - Filtrar reportes del ciudadano por estado
    @Query(value = "SELECT r.idReporte " +
            "FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE r.idUsuario = :idUsuario " +
            "AND dr.estado = :estado",
            nativeQuery = true)
    List<Object> findIdsByUsuarioAndEstado(
            @Param("idUsuario") int idUsuario,
            @Param("estado") String estado);

    //Modulo 4.1 - Listar todos los reportes para el panel admin con filtros opcionales
    @Query(value = "SELECT r.idReporte " +
            "FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "INNER JOIN municipios m ON dr.idMunicipio = m.idMunicipio " +
            "LEFT JOIN reporteAsignadoCuadrilla rac ON r.idReporte = rac.idReporte " +
            "WHERE (:estado IS NULL OR dr.estado = :estado) " +
            "AND (:idMunicipio IS NULL OR dr.idMunicipio = :idMunicipio) " +
            "AND (:fechaInicio IS NULL OR dr.fechaRegistro >= TO_DATE(:fechaInicio, 'YYYY-MM-DD')) " +
            "AND (:fechaFin IS NULL OR dr.fechaRegistro <= TO_DATE(:fechaFin, 'YYYY-MM-DD')) " +
            "AND (:keyword IS NULL OR r.titulo LIKE '%' || :keyword || '%' " +
            "     OR dr.descripcion LIKE '%' || :keyword || '%')",
            nativeQuery = true)
    List<Object> findIdsWithFilters(
            @Param("estado") String estado,
            @Param("idMunicipio") Integer idMunicipio,
            @Param("fechaInicio") String fechaInicio,
            @Param("fechaFin") String fechaFin,
            @Param("keyword") String keyword);

    //Modulo 13 - Feed publico de reportes del municipio del ciudadano
    @Query(value = "SELECT r.idReporte " +
            "FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "INNER JOIN usuario u ON r.idUsuario = u.idUsuario " +
            "WHERE dr.idMunicipio = :idMunicipio " +
            "AND dr.estado NOT IN ('Cancelado') " +
            "ORDER BY dr.fechaRegistro DESC",
            nativeQuery = true)
    List<Object> findFeedIdsByMunicipio(@Param("idMunicipio") int idMunicipio);
}