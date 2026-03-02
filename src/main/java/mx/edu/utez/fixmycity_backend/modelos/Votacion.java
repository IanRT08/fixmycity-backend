package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

import java.sql.Timestamp;

public class Votacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idVoto;

    @ManyToOne
    @JoinColumn(name = "idReporte", nullable = false)
    private Reporte idReporte;

    @ManyToOne
    @JoinColumn(name = "idCuadrilla", nullable = false)
    private Cuadrilla idCuadrilla;

    @ManyToOne
    @JoinColumn(name = "idVoluntario", nullable = false)
    private Voluntario idVoluntario;

    @Column(name = "respuesta", nullable = false, length = 10)
    private String respuesta;

    @Column(name = "fechaVoto", nullable = false)
    private Timestamp fechaVoto;

    public int getIdVoto() {
        return idVoto;
    }

    public void setIdVoto(int idVoto) {
        this.idVoto = idVoto;
    }

    public Reporte getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Reporte idReporte) {
        this.idReporte = idReporte;
    }

    public Cuadrilla getIdCuadrilla() {
        return idCuadrilla;
    }

    public void setIdCuadrilla(Cuadrilla idCuadrilla) {
        this.idCuadrilla = idCuadrilla;
    }

    public Voluntario getIdVoluntario() {
        return idVoluntario;
    }

    public void setIdVoluntario(Voluntario idVoluntario) {
        this.idVoluntario = idVoluntario;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public Timestamp getFechaVoto() {
        return fechaVoto;
    }

    public void setFechaVoto(Timestamp fechaVoto) {
        this.fechaVoto = fechaVoto;
    }
}
