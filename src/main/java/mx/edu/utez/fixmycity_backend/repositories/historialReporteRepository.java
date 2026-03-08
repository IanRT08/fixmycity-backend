package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.historialReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface historialReporteRepository extends JpaRepository<historialReporte, Integer> {

    //Modulo 4.4 - Consultar todos los cambios de estado de un reporte
    @Query(value = "SELECT h.idCambioEstado, h.idReporte, h.idResponsable, " +
            "h.fechaCambio, h.estadoAnterior, h.estadoNuevo " +
            "FROM historialReporte h " +
            "INNER JOIN reporte r ON h.idReporte = r.idReporte " +
            "INNER JOIN usuario u ON h.idResponsable = u.idUsuario " +
            "WHERE h.idReporte = :idReporte " +
            "ORDER BY h.fechaCambio ASC",
            nativeQuery = true)
    List<historialReporte> findByReporte(@Param("idReporte") int idReporte);
}