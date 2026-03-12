package mx.edu.utez.fixmycity_backend.dto.response;

public class MunicipioResponse {

    private int idMunicipio;
    private String nombre;
    private String estado;

    public MunicipioResponse(int idMunicipio, String nombre, String estado) {
        this.idMunicipio = idMunicipio;
        this.nombre = nombre;
        this.estado = estado;
    }

    public int getIdMunicipio() {
        return idMunicipio;
    }
    public void setIdMunicipio(int idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}