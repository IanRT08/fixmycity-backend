package mx.edu.utez.fixmycity_backend.modelos;

import jakarta.persistence.*;

@Entity
@Table(name = "fotosReporte")
public class fotosReporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idFoto;

    @ManyToOne
    @JoinColumn(name = "idReporte", nullable = false)
    private Reporte idReporte;

    @Column(name = "foto")
    private String foto;

    public int getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(int idFoto) {
        this.idFoto = idFoto;
    }

    public Reporte getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Reporte idReporte) {
        this.idReporte = idReporte;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
