package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.cancelacionReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface cancelacionReporteRepository extends JpaRepository<cancelacionReporte, Integer> {

    //Modulo 2.4 - Obtener el motivo de cancelacion de un reporte
    @Query(value = "SELECT cr.idCancelacion, cr.idReporte, cr.idUsuario, cr.motivoCancelacion " +
            "FROM cancelacionReporte cr " +
            "INNER JOIN reporte r ON cr.idReporte = r.idReporte " +
            "INNER JOIN usuario u ON cr.idUsuario = u.idUsuario " +
            "WHERE cr.idReporte = :idReporte",
            nativeQuery = true)
    Optional<cancelacionReporte> findByReporte(@Param("idReporte") int idReporte);
}