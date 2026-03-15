package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.solicitudVoluntario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudVoluntarioRepository extends JpaRepository<solicitudVoluntario, Integer> {

    //Modulo 1.3 - Verificar si el usuario ya tiene una solicitud pendiente
    @Query(value = "SELECT s.idSolicitud " +
            "FROM solicitudVoluntario s " +
            "INNER JOIN usuario u ON s.idUsuario = u.idUsuario " +
            "WHERE s.idUsuario = :idUsuario " +
            "AND s.estado = 'pendiente'",
            nativeQuery = true)
    Optional<Object> findPendingIdByUsuario(@Param("idUsuario") int idUsuario);

    //Modulo 1.3 - Listar todas las solicitudes pendientes sin filtro de municipio (superadmin)
    @Query(value = "SELECT s.idSolicitud " +
            "FROM solicitudVoluntario s " +
            "WHERE s.estado = :estado",
            nativeQuery = true)
    List<Object> findIdsByEstado(@Param("estado") String estado);

    //Modulo 1.3 - Listar solicitudes pendientes filtradas por municipio del admin
    @Query(value = "SELECT s.idSolicitud " +
            "FROM solicitudVoluntario s " +
            "INNER JOIN usuario u ON s.idUsuario = u.idUsuario " +
            "INNER JOIN informacionVoluntario iv ON s.idSolicitud = iv.idSolicitud " +
            "WHERE s.estado = :estado " +
            "AND u.idMunicipio = :idMunicipio",
            nativeQuery = true)
    List<Object> findIdsByEstadoAndMunicipio(@Param("estado") String estado, @Param("idMunicipio") int idMunicipio);

//Modulo 1.3 - Aprobar o rechazar una solicitud de voluntario
    @Modifying
    @Transactional
    @Query(value = "UPDATE solicitudVoluntario SET estado = :estado " +
            "WHERE idSolicitud = :idSolicitud",
            nativeQuery = true)
    void updateEstado(@Param("idSolicitud") int idSolicitud, @Param("estado") String estado);
}