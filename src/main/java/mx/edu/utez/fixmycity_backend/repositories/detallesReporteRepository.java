package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.detallesReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface detallesReporteRepository extends JpaRepository<detallesReporte, Integer> {

    //Modulo 2.1 - Obtener detalles de un reporte especifico
    @Query(value = "SELECT dr.idReporte, dr.descripcion, dr.idMunicipio, dr.estado, dr.fechaRegistro " +
            "FROM detallesReporte dr " +
            "INNER JOIN reporte r ON dr.idReporte = r.idReporte " +
            "WHERE dr.idReporte = :idReporte",
            nativeQuery = true)
    Optional<detallesReporte> findByReporte(@Param("idReporte") int idReporte);

    //Modulos 3.2, 3.4, 3.5, 3.6 - Cambiar estado del reporte en el flujo de atencion
    @Modifying
    @Transactional
    @Query(value = "UPDATE detallesReporte SET estado = :estado " +
            "WHERE idReporte = :idReporte",
            nativeQuery = true)
    void updateEstado(@Param("idReporte") int idReporte, @Param("estado") String estado);
}