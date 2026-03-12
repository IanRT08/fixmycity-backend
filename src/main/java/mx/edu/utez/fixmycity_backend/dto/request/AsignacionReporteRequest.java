package mx.edu.utez.fixmycity_backend.dto.request;

import jakarta.validation.constraints.*;

public class AsignacionReporteRequest {

    @NotNull(message = "El reporte es obligatorio")
    private Integer idReporte;

    @NotNull(message = "La cuadrilla es obligatoria")
    private Integer idCuadrilla;

    public Integer getIdReporte() {
        return idReporte;
    }
    public void setIdReporte(Integer idReporte) {
        this.idReporte = idReporte;
    }

    public Integer getIdCuadrilla() {
        return idCuadrilla;
    }
    public void setIdCuadrilla(Integer idCuadrilla) {
        this.idCuadrilla = idCuadrilla;
    }
}