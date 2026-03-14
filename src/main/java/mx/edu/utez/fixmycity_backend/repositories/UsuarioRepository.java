package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    //Modulo 1.2 - Buscar usuario por nombre para el inicio de sesion
    @Query(value = "SELECT u.idUsuario FROM usuario u " +
            "INNER JOIN municipios m ON u.idMunicipio = m.idMunicipio " +
            "WHERE u.nombreUsuario = :nombreUsuario AND u.estado = :estado",
            nativeQuery = true)
    Optional<Object> buscarIdPorNombreYEstado(
            @Param("nombreUsuario") String nombreUsuario,
            @Param("estado") String estado);

    //Buscar usuario por nombre sin filtro de estado (para JWT filter)
    @Query(value = "SELECT u.idUsuario FROM usuario u " +
            "WHERE u.nombreUsuario = :nombreUsuario",
            nativeQuery = true)
    Optional<Object> buscarIdPorNombre(@Param("nombreUsuario") String nombreUsuario);

    //Modulo 1.1 - Verificar si el correo ya existe antes de registrar un ciudadano
    @Query(value = "SELECT u.idUsuario FROM usuario u " +
            "WHERE u.correo = :correo",
            nativeQuery = true)
    Optional<Object> buscarIdPorCorreo(@Param("correo") String correo);

    //Modulo 1.4 - Listar todos los usuarios por tipo de rol (ciudadano, voluntario, admin)
    @Query(value = "SELECT u.idUsuario FROM usuario u " +
            "INNER JOIN municipios m ON u.idMunicipio = m.idMunicipio " +
            "WHERE u.tipo = :tipo",
            nativeQuery = true)
    List<Object> buscarIdsPorTipo(@Param("tipo") String tipo);

    //Modulo 1.4 - Listar usuarios por estado
    @Query(value = "SELECT u.idUsuario FROM usuario u " +
            "WHERE u.estado = :estado",
            nativeQuery = true)
    List<Object> buscarIdsPorEstado(@Param("estado") String estado);

    //Modulo 1.4 - Activar o desactivar cuenta de usuario
    @Modifying
    @Transactional
    @Query(value = "UPDATE usuario SET estado = :estado " +
            "WHERE idUsuario = :id",
            nativeQuery = true)
    void updateEstado(@Param("id") int id, @Param("estado") String estado);

    //Modulo 1.4 - Cambiar el tipo de usuario (ciudadano a voluntario tras aprobacion)
    @Modifying
    @Transactional
    @Query(value = "UPDATE usuario SET tipo = :tipo " +
            "WHERE idUsuario = :id",
            nativeQuery = true)
    void updateTipo(@Param("id") int id, @Param("tipo") String tipo);
}