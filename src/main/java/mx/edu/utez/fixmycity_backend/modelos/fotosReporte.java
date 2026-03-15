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
    private Reporte reporte;

    @Lob
    @Column(name = "foto")
    private byte[] foto;

    public int getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(int idFoto) {
        this.idFoto = idFoto;
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }
}
