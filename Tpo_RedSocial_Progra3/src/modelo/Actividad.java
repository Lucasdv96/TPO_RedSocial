package modelo;

import java.time.LocalTime;

public class Actividad {

    private final String id;
    private final LocalTime inicio;
    private final LocalTime fin;
    private final int interes;

    public Actividad(String id, LocalTime inicio, LocalTime fin, int interes) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("La actividad " + id + " necesita hora de inicio y de fin");
        }
        if (!inicio.isBefore(fin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la de fin: " + id);
        }
        if (interes < 0) {
            throw new IllegalArgumentException("El interes no puede ser negativo: " + id);
        }
        this.id = id;
        this.inicio = inicio;
        this.fin = fin;
        this.interes = interes;
    }

    public String getId() { return id; }
    public LocalTime getInicio() { return inicio; }
    public LocalTime getFin() { return fin; }
    public int getInteres() { return interes; }

    @Override
    public String toString() {
        return id + " (" + inicio + " - " + fin + ", interes " + interes + ")";
    }
}
