package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Integer> {

    @Query(value = "SELECT r.idReporte FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE r.idUsuario = :idUsuario",
            nativeQuery = true)
    List<Object> findIdsByUsuario(@Param("idUsuario") int idUsuario);

    @Query(value = "SELECT r.idReporte FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE r.idUsuario = :idUsuario AND dr.estado = :estado",
            nativeQuery = true)
    List<Object> findIdsByUsuarioAndEstado(
            @Param("idUsuario") int idUsuario, @Param("estado") String estado);

    @Query(value = "SELECT r.idReporte, r.titulo, u.nombreUsuario, " +
            "dr.descripcion, dr.estado, dr.fechaRegistro, m.nombre AS municipio " +
            "FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "INNER JOIN municipios m ON dr.idMunicipio = m.idMunicipio " +
            "INNER JOIN usuario u ON r.idUsuario = u.idUsuario " +
            "WHERE dr.estado = NVL(:estado, dr.estado) " +
            "AND dr.idMunicipio = NVL(:idMunicipio, dr.idMunicipio) " +
            "AND dr.fechaRegistro >= NVL(TO_DATE(:fechaInicio, 'YYYY-MM-DD'), dr.fechaRegistro) " +
            "AND dr.fechaRegistro <= NVL(TO_DATE(:fechaFin, 'YYYY-MM-DD'), dr.fechaRegistro) " +
            "AND (:keyword IS NULL OR UPPER(r.titulo) LIKE UPPER('%' || :keyword || '%') " +
            "     OR UPPER(dr.descripcion) LIKE UPPER('%' || :keyword || '%')) " +
            "ORDER BY dr.fechaRegistro DESC",
            nativeQuery = true)
    List<Object[]> findReportesAdminDirect(
            @Param("estado") String estado, @Param("idMunicipio") Integer idMunicipio,
            @Param("fechaInicio") String fechaInicio, @Param("fechaFin") String fechaFin,
            @Param("keyword") String keyword);

    @Query(value = "SELECT r.idReporte FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "INNER JOIN municipios m ON dr.idMunicipio = m.idMunicipio " +
            "WHERE dr.estado = NVL(:estado, dr.estado) " +
            "AND dr.idMunicipio = NVL(:idMunicipio, dr.idMunicipio) " +
            "AND dr.fechaRegistro >= NVL(TO_DATE(:fechaInicio, 'YYYY-MM-DD'), dr.fechaRegistro) " +
            "AND dr.fechaRegistro <= NVL(TO_DATE(:fechaFin, 'YYYY-MM-DD'), dr.fechaRegistro) " +
            "AND (:keyword IS NULL OR UPPER(r.titulo) LIKE UPPER('%' || :keyword || '%') " +
            "     OR UPPER(dr.descripcion) LIKE UPPER('%' || :keyword || '%')) " +
            "ORDER BY dr.fechaRegistro DESC",
            nativeQuery = true)
    List<Object> findIdsWithFilters(
            @Param("estado") String estado, @Param("idMunicipio") Integer idMunicipio,
            @Param("fechaInicio") String fechaInicio, @Param("fechaFin") String fechaFin,
            @Param("keyword") String keyword);

    // --- Dashboard COUNT queries (optimized, no N+1) ---
    @Query(value = "SELECT COUNT(r.idReporte) FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE dr.estado = :estado " +
            "AND dr.idMunicipio = NVL(:idMunicipio, dr.idMunicipio)",
            nativeQuery = true)
    int countByEstadoAndMunicipio(@Param("estado") String estado, @Param("idMunicipio") Integer idMunicipio);

    @Query(value = "SELECT COUNT(r.idReporte) FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE TRUNC(dr.fechaRegistro) = TRUNC(SYSDATE) " +
            "AND dr.idMunicipio = NVL(:idMunicipio, dr.idMunicipio)",
            nativeQuery = true)
    int countReportesHoy(@Param("idMunicipio") Integer idMunicipio);

    @Query(value = "SELECT m.nombre AS municipio, COUNT(r.idReporte) AS count " +
            "FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "INNER JOIN municipios m ON dr.idMunicipio = m.idMunicipio " +
            "WHERE dr.estado IN ('Pendiente', 'Asignado', 'En camino', 'En curso') " +
            "GROUP BY m.nombre ORDER BY count DESC",
            nativeQuery = true)
    List<Object[]> countReportesActivosPorMunicipio();

    @Query(value = "SELECT r.idReporte FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE dr.idMunicipio = :idMunicipio AND dr.estado NOT IN ('Cancelado') " +
            "ORDER BY dr.fechaRegistro DESC",
            nativeQuery = true)
    List<Object> findFeedIdsByMunicipio(@Param("idMunicipio") int idMunicipio);

    @Query(value = "SELECT COUNT(r.idReporte) FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE dr.idMunicipio = :idMunicipio AND dr.estado NOT IN ('Cancelado')",
            nativeQuery = true)
    long countFeedByMunicipio(@Param("idMunicipio") int idMunicipio);

    @Query(value = "SELECT r.idReporte FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE dr.idMunicipio = :idMunicipio AND dr.estado NOT IN ('Cancelado') " +
            "ORDER BY dr.fechaRegistro DESC " +
            "OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY",
            nativeQuery = true)
    List<Object> findFeedIdsByMunicipioPaged(
            @Param("idMunicipio") int idMunicipio,
            @Param("offset") int offset, @Param("limit") int limit);

    @Query(value = "SELECT COUNT(r.idReporte) FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE r.idUsuario = :idUsuario",
            nativeQuery = true)
    long countReportesByUsuario(@Param("idUsuario") int idUsuario);

    @Query(value = "SELECT r.idReporte FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE r.idUsuario = :idUsuario " +
            "ORDER BY dr.fechaRegistro DESC " +
            "OFFSET :offset ROWS FETCH NEXT :limit ROWS ONLY",
            nativeQuery = true)
    List<Object> findIdsByUsuarioPaged(
            @Param("idUsuario") int idUsuario,
            @Param("offset") int offset, @Param("limit") int limit);

    // Dashboard count: reportes en proceso (Asignado + En camino + En curso)
    @Query(value = "SELECT COUNT(r.idReporte) FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE dr.estado IN ('Asignado', 'En camino', 'En curso') " +
            "AND dr.idMunicipio = NVL(:idMunicipio, dr.idMunicipio)",
            nativeQuery = true)
    int countReportesEnProceso(@Param("idMunicipio") Integer idMunicipio);

    // Dashboard count by date range
    @Query(value = "SELECT COUNT(r.idReporte) FROM reporte r " +
            "INNER JOIN detallesReporte dr ON r.idReporte = dr.idReporte " +
            "WHERE dr.estado = NVL(:estado, dr.estado) " +
            "AND dr.fechaRegistro >= TO_DATE(:fechaInicio, 'YYYY-MM-DD') " +
            "AND dr.fechaRegistro <= TO_DATE(:fechaFin, 'YYYY-MM-DD')",
            nativeQuery = true)
    int countByEstadoAndFecha(@Param("estado") String estado,
                              @Param("fechaInicio") String fechaInicio,
                              @Param("fechaFin") String fechaFin);
}
