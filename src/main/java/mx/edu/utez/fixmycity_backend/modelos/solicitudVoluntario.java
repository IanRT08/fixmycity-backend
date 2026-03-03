package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "solicitudVoluntario")
public class solicitudVoluntario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSolicitud;

    @ManyToOne
    @JoinColumn(name = "idUsuario", nullable = false)
    private Usuario usuario;

    @Column(name = "estado", nullable = false)
    private String estado;

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
