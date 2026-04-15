package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.miembrosCuadrilla;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface miembrosCuadrillaRepository extends JpaRepository<miembrosCuadrilla, Integer> {

    List<miembrosCuadrilla> findByVoluntario_IdVoluntario(int idVoluntario);

    List<miembrosCuadrilla> findByCuadrilla_IdCuadrillaOrderByNumeroVoluntarioAsc(int idCuadrilla);

    //Modulo 3.1 - Listar todos los miembros de una cuadrilla
    @Query(value = "SELECT mc.numeroVoluntario, mc.idCuadrilla, mc.idMiembro, mc.tipo " +
            "FROM miembrosCuadrilla mc " +
            "INNER JOIN cuadrilla c ON mc.idCuadrilla = c.idCuadrilla " +
            "INNER JOIN voluntario v ON mc.idMiembro = v.idVoluntario " +
            "INNER JOIN usuario u ON v.idUsuario = u.idUsuario " +
            "WHERE mc.idCuadrilla = :idCuadrilla",
            nativeQuery = true)
    List<miembrosCuadrilla> findByCuadrilla(@Param("idCuadrilla") int idCuadrilla);

    //Modulo 3.1 - Contar miembros de una cuadrilla para validar que sean exactamente 5
    @Query(value = "SELECT COUNT(mc.numeroVoluntario) " +
            "FROM miembrosCuadrilla mc " +
            "WHERE mc.idCuadrilla = :idCuadrilla",
            nativeQuery = true)
    int countByCuadrilla(@Param("idCuadrilla") int idCuadrilla);

    //Modulo 3.1 - Eliminar todos los miembros de una cuadrilla (para edición de integrantes)
    @Modifying
    @Query(value = "DELETE FROM miembrosCuadrilla WHERE idCuadrilla = :idCuadrilla", nativeQuery = true)
    void deleteAllByCuadrilla(@Param("idCuadrilla") int idCuadrilla);

    //Modulo 3.3 - Verificar si un voluntario pertenece a la cuadrilla asignada al reporte
    @Query(value = "SELECT mc.numeroVoluntario, mc.idCuadrilla, mc.idMiembro, mc.tipo " +
            "FROM miembrosCuadrilla mc " +
            "INNER JOIN reporteAsignadoCuadrilla rac ON mc.idCuadrilla = rac.idCuadrilla " +
            "WHERE rac.idReporte = :idReporte " +
            "AND mc.idMiembro = :idVoluntario",
            nativeQuery = true)
    List<miembrosCuadrilla> findByReporteAndVoluntario(
            @Param("idReporte") int idReporte,
            @Param("idVoluntario") int idVoluntario);
}