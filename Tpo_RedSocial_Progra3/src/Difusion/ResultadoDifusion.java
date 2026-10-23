package Difusion;

import java.util.*;

public class ResultadoDifusion {
    private final boolean alcanzable;
    private final List<Integer> cadena;   // origen ... destino (vacía si no es alcanzable)
    private final int tiempoTotal;        // -1 si no es alcanzable
    private final List<String> pasos;     // log de decisiones del algoritmo

    public ResultadoDifusion(boolean alcanzable, List<Integer> cadena,
                             int tiempoTotal, List<String> pasos) {
        this.alcanzable = alcanzable;
        this.cadena = Collections.unmodifiableList(new ArrayList<>(cadena));
        this.tiempoTotal = tiempoTotal;
        this.pasos = Collections.unmodifiableList(new ArrayList<>(pasos));
    }

    public boolean isAlcanzable() { return alcanzable; }
    public List<Integer> getCadena() { return cadena; }
    public int getTiempoTotal() { return tiempoTotal; }
    public List<String> getPasos() { return pasos; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (alcanzable) {
            sb.append("Cadena: ");
            for (int i = 0; i < cadena.size(); i++) {
                if (i > 0) sb.append(" -> ");
                sb.append(cadena.get(i));
            }
            sb.append("\nUsuarios intermedios: ").append(Math.max(0, cadena.size() - 2));
            sb.append("\nTiempo total: ").append(tiempoTotal).append(" min");
        } else {
            sb.append("El destino NO es alcanzable desde el origen.");
        }
        sb.append("\n--- Decisiones del cálculo ---\n");
        for (String p : pasos) sb.append("  ").append(p).append("\n");
        return sb.toString();
    }
}