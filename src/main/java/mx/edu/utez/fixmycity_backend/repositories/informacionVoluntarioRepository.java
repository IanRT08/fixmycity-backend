package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.informacionVoluntario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface informacionVoluntarioRepository extends JpaRepository<informacionVoluntario, Integer> {

    //Modulo 1.3 - Verificar si el CURP ya está registrado en el sistema
    @Query(value = "SELECT iv.idInfo, iv.idSolicitud, iv.nombre, iv.CURP, " +
            "iv.telefono, iv.idMunicipio, iv.descripcion " +
            "FROM informacionVoluntario iv " +
            "WHERE iv.CURP = :curp",
            nativeQuery = true)
    Optional<informacionVoluntario> findByCURP(@Param("curp") String curp);

    //Modulo 1.3 - Obtener la informacion completa de una solicitud para revisión del admin
    @Query(value = "SELECT iv.idInfo, iv.idSolicitud, iv.nombre, iv.CURP, " +
            "iv.telefono, iv.idMunicipio, iv.descripcion " +
            "FROM informacionVoluntario iv " +
            "INNER JOIN solicitudVoluntario s ON iv.idSolicitud = s.idSolicitud " +
            "INNER JOIN usuario u ON s.idUsuario = u.idUsuario " +
            "WHERE iv.idSolicitud = :idSolicitud",
            nativeQuery = true)
    Optional<informacionVoluntario> findBySolicitud(@Param("idSolicitud") int idSolicitud);
}