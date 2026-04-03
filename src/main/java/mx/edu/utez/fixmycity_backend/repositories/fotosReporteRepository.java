package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.fotosReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface fotosReporteRepository extends JpaRepository<fotosReporte, Integer> {

    //Modulo 2.1 - Obtener todas las fotos de un reporte
    @Query(value = "SELECT f.idFoto, f.idReporte, f.foto " +
            "FROM fotosReporte f " +
            "INNER JOIN reporte r ON f.idReporte = r.idReporte " +
            "WHERE f.idReporte = :idReporte",
            nativeQuery = true)
    List<fotosReporte> findByReporte(@Param("idReporte") int idReporte);

    //Modulo 2.1 - Obtener solo la primera foto de un reporte (para listados y feed)
    @Query(value = "SELECT * FROM fotosReporte f WHERE f.idReporte = :idReporte AND ROWNUM = 1",
            nativeQuery = true)
    Optional<fotosReporte> findFirstByReporte(@Param("idReporte") int idReporte);

    //Modulo 2.1 - Contar fotos de un reporte para validar el maximo de 3
    @Query(value = "SELECT COUNT(f.idFoto) " +
            "FROM fotosReporte f " +
            "WHERE f.idReporte = :idReporte",
            nativeQuery = true)
    int countByReporte(@Param("idReporte") int idReporte);
}