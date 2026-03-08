package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.Notificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificacionesRepository extends JpaRepository<Notificaciones, Integer> {

    //Modulo 5.1 - Obtener todas las notificaciones de un usuario
    @Query(value = "SELECT n.idNotificacion, n.idUsuario, n.idReporte, " +
            "n.mensaje, n.leida, n.fechaEnvio " +
            "FROM notificaciones n " +
            "INNER JOIN usuario u ON n.idUsuario = u.idUsuario " +
            "INNER JOIN reporte r ON n.idReporte = r.idReporte " +
            "WHERE n.idUsuario = :idUsuario " +
            "ORDER BY n.fechaEnvio DESC",
            nativeQuery = true)
    List<Notificaciones> findByUsuario(@Param("idUsuario") int idUsuario);

    //Modulo 5.1 - Obtener notificaciones no leídas de un usuario
    @Query(value = "SELECT n.idNotificacion, n.idUsuario, n.idReporte, " +
            "n.mensaje, n.leida, n.fechaEnvio " +
            "FROM notificaciones n " +
            "WHERE n.idUsuario = :idUsuario " +
            "AND n.leida = 0 " +
            "ORDER BY n.fechaEnvio DESC",
            nativeQuery = true)
    List<Notificaciones> findUnreadByUsuario(@Param("idUsuario") int idUsuario);

    //Modulo 5.1 - Marcar una notificacion como leida
    @Modifying
    @Transactional
    @Query(value = "UPDATE notificaciones SET leida = 1 " +
            "WHERE idNotificacion = :idNotificacion " +
            "AND idUsuario = :idUsuario",
            nativeQuery = true)
    void markAsRead(
            @Param("idNotificacion") int idNotificacion,
            @Param("idUsuario") int idUsuario);
}