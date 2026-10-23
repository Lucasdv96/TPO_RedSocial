
public class Iniciativa {

    private final String id;
    private final int costo;
    private final int impacto;

    public Iniciativa(String id, int costo, int impacto) {
        if (costo <= 0) {
            throw new IllegalArgumentException("El costo debe ser un entero positivo: " + id);
        }
        if (impacto < 0) {
            throw new IllegalArgumentException("El impacto no puede ser negativo: " + id);
        }
        this.id = id;
        this.costo = costo;
        this.impacto = impacto;
    }

    public String getId() {
        return id;
    }

    public int getCosto() {
        return costo;
    }

    public int getImpacto() {
        return impacto;
    }

    @Override
    public String toString() {
        return id + " (costo " + costo + ", impacto " + impacto + ")";
    }
}
