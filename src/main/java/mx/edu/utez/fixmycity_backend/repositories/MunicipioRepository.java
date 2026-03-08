package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.Municipios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipios, Integer> {

    //Modulo 6.1 - Verificar si ya existe un municipio con ese nombre antes de crearlo
    @Query(value = "SELECT m.idMunicipio, m.nombre, m.estado " +
            "FROM municipios m " +
            "WHERE m.nombre = :nombre",
            nativeQuery = true)
    Optional<Municipios> findByNombre(@Param("nombre") String nombre);

    //Modulo 6.1 - Listar municipios según su estado (Activo/Inactivo)
    @Query(value = "SELECT m.idMunicipio, m.nombre, m.estado " +
            "FROM municipios m " +
            "WHERE m.estado = :estado",
            nativeQuery = true)
    List<Municipios> findByEstado(@Param("estado") String estado);

    //Modulo 6.1 - Habilitar o deshabilitar un municipio (solo si no tiene reportes en proceso)
    @Modifying
    @Transactional
    @Query(value = "UPDATE municipios SET estado = :estado " +
            "WHERE idMunicipio = :id " +
            "AND NOT EXISTS (" +
            "   SELECT 1 FROM detallesReporte dr " +
            "   WHERE dr.idMunicipio = :id " +
            "   AND dr.estado IN ('Pendiente', 'Asignado', 'En camino', 'En curso'))",
            nativeQuery = true)
    int updateEstado(@Param("id") int id, @Param("estado") String estado);
}