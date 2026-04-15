package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoluntarioRepository extends JpaRepository<Voluntario, Integer> {

    //Modulo 1.3 - Buscar voluntario por su usuario para verificar si ya existe
    @Query(value = "SELECT v.idVoluntario " +
            "FROM voluntario v " +
            "INNER JOIN usuario u ON v.idUsuario = u.idUsuario " +
            "WHERE v.idUsuario = :idUsuario",
            nativeQuery = true)
    Optional<Object> findIdByUsuario(@Param("idUsuario") int idUsuario);

    //Modulo 3.1 - Listar voluntarios aprobados de un municipio para formar cuadrilla
    @Query(value = "SELECT v.idVoluntario " +
            "FROM voluntario v " +
            "INNER JOIN usuario u ON v.idUsuario = u.idUsuario " +
            "INNER JOIN municipios m ON u.idMunicipio = m.idMunicipio " +
            "WHERE u.idMunicipio = :idMunicipio " +
            "AND u.estado = 'activo' " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM miembrosCuadrilla mc " +
            "   INNER JOIN cuadrilla c ON mc.idCuadrilla = c.idCuadrilla " +
            "   WHERE mc.idMiembro = v.idVoluntario " +
            "   AND c.estado = 'activa')",
            nativeQuery = true)
    List<Object> findAvailableIdsByMunicipio(@Param("idMunicipio") int idMunicipio);

    //Modulo 3.1 - Listar voluntarios disponibles para editar una cuadrilla (incluye miembros actuales)
    @Query(value = "SELECT v.idVoluntario " +
            "FROM voluntario v " +
            "INNER JOIN usuario u ON v.idUsuario = u.idUsuario " +
            "WHERE u.idMunicipio = :idMunicipio " +
            "AND u.estado = 'activo' " +
            "AND (NOT EXISTS (" +
            "   SELECT 1 FROM miembrosCuadrilla mc " +
            "   INNER JOIN cuadrilla c ON mc.idCuadrilla = c.idCuadrilla " +
            "   WHERE mc.idMiembro = v.idVoluntario " +
            "   AND c.estado = 'activa') " +
            "OR EXISTS (" +
            "   SELECT 1 FROM miembrosCuadrilla mc2 " +
            "   WHERE mc2.idMiembro = v.idVoluntario " +
            "   AND mc2.idCuadrilla = :idCuadrilla))",
            nativeQuery = true)
    List<Object> findAvailableIdsByMunicipioForEdit(@Param("idMunicipio") int idMunicipio,
                                                    @Param("idCuadrilla") int idCuadrilla);
}