package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.TokenRecuperacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Integer> {

    @Query(value = "SELECT t.idToken " +
            "FROM tokenRecuperacion t " +
            "INNER JOIN usuario u ON t.idUsuario = u.idUsuario " +
            "WHERE u.correo = :correo AND t.token = :token AND t.usado = 0 " +
            "AND t.fechaExpiracion > SYSDATE",
            nativeQuery = true)
    Optional<Object> findValidToken(
            @Param("correo") String correo,
            @Param("token") String token);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tokenRecuperacion SET usado = 1 WHERE idToken = :idToken",
            nativeQuery = true)
    void marcarComoUsado(@Param("idToken") int idToken);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tokenRecuperacion WHERE idUsuario = :idUsuario",
            nativeQuery = true)
    void deleteByUsuario(@Param("idUsuario") int idUsuario);
}
