package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.PreferenciasNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreferenciasNotificacionRepository extends JpaRepository<PreferenciasNotificacion, Integer> {

    Optional<PreferenciasNotificacion> findByIdUsuario(int idUsuario);
}
