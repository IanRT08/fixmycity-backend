package mx.edu.utez.fixmycity_backend.dto.response;

import java.sql.Timestamp;

public class HistorialReporteResponse {

    private int idCambioEstado;
    private String estadoAnterior;
    private String estadoNuevo;
    private Timestamp fechaCambio;
    private String responsable;

    public HistorialReporteResponse(int idCambioEstado, String estadoAnterior,
                                    String estadoNuevo, Timestamp fechaCambio,
                                    String responsable) {
        this.idCambioEstado = idCambioEstado;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaCambio = fechaCambio;
        this.responsable = responsable;
    }

    public int getIdCambioEstado() {
        return idCambioEstado;
    }
    public void setIdCambioEstado(int idCambioEstado) {
        this.idCambioEstado = idCambioEstado;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }
    public void setEstadoAnterior(String estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }
    public void setEstadoNuevo(String estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public Timestamp getFechaCambio() {
        return fechaCambio;
    }
    public void setFechaCambio(Timestamp fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public String getResponsable() {
        return responsable;
    }
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
}