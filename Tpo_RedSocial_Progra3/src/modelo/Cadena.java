package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Una cadena de difusion: la secuencia de usuarios desde el origen hasta el destino y su tiempo total. */
public class Cadena {

    private final List<Integer> usuarios;
    private final int tiempoTotal;

    public Cadena(List<Integer> usuarios, int tiempoTotal) {
        this.usuarios = Collections.unmodifiableList(new ArrayList<>(usuarios));
        this.tiempoTotal = tiempoTotal;
    }

    public List<Integer> getUsuarios() { return usuarios; }
    public int getTiempoTotal() { return tiempoTotal; }

    // Intermediarios = usuarios entre el origen y el destino (sin contar los extremos)
    public int getIntermediarios() {
        return Math.max(0, usuarios.size() - 2);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < usuarios.size(); i++) {
            if (i > 0) sb.append(" -> ");
            sb.append(usuarios.get(i));
        }
        return sb.toString();
    }
}