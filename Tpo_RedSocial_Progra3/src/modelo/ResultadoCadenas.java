package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Resultado de la busqueda de cadenas alternativas de difusion. */
public class ResultadoCadenas {

    private final List<Cadena> cadenas;   // ordenadas por tiempo, luego intermediarios
    private final List<Cadena> mejores;   // las de menor tiempo (y menos intermediarios)
    private final String aviso;           // motivo si no se pudo buscar, o null
    private final int llamadas;           // nodos explorados por el backtracking
    private final int podas;              // extensiones descartadas

    public ResultadoCadenas(List<Cadena> cadenas, List<Cadena> mejores, String aviso, int llamadas, int podas) {
        this.cadenas = Collections.unmodifiableList(new ArrayList<>(cadenas));
        this.mejores = Collections.unmodifiableList(new ArrayList<>(mejores));
        this.aviso = aviso;
        this.llamadas = llamadas;
        this.podas = podas;
    }

    public List<Cadena> getCadenas() { return cadenas; }
    public List<Cadena> getMejores() { return mejores; }
    public String getAviso() { return aviso; }
    public int getLlamadas() { return llamadas; }
    public int getPodas() { return podas; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (aviso != null) {
            sb.append(aviso).append("\n");
            return sb.toString();
        }
        sb.append("Cadenas validas encontradas: ").append(cadenas.size()).append("\n");
        for (int i = 0; i < cadenas.size(); i++) {
            Cadena c = cadenas.get(i);
            sb.append(String.format("  %d) %-25s %4d min   %d intermediario(s)%n",
                    i + 1, c, c.getTiempoTotal(), c.getIntermediarios()));
        }
        if (cadenas.isEmpty()) {
            sb.append("No hay cadenas que cumplan las condiciones.\n");
        } else {
            sb.append("Menor tiempo total: ").append(mejores.get(0).getTiempoTotal()).append(" min\n");
            for (Cadena c : mejores) {
                sb.append("  ").append(c).append("\n");
            }
        }
        sb.append("Nodos explorados: ").append(llamadas).append(" | Podas: ").append(podas).append("\n");
        return sb.toString();
    }
}