import java.time.LocalDateTime;

public class Publicacion {
    private final int id;
    private final String autor;
    private final LocalDateTime fechaHora;
    private final int reacciones;
    private final int comentarios;

    public Publicacion(int id, String autor, LocalDateTime fechaHora, int reacciones, int comentarios) {
        this.id = id;
        this.autor = autor;
        this.fechaHora = fechaHora;
        this.reacciones = reacciones;
        this.comentarios = comentarios;
    }

    public int getId() { return id; }
    public String getAutor() { return autor; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getReacciones() { return reacciones; }
    public int getComentarios() { return comentarios; }

    // RELEVANCIA = Reacciones + comentarios. Si el equipo decide otra formula, se cambia solo aca.

    public int getRelevancia(){
        return reacciones +  comentarios;
    }

    @Override
    public String toString() {
        return "Publicacion{id=" + id + ", autor=" + autor + ", fecha=" + fechaHora
                + ", reacciones=" + reacciones + ", comentarios=" + comentarios
                + ", relevancia=" + getRelevancia() + "}";
    }
}
