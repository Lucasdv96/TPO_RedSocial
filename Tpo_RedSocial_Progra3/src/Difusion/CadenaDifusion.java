package Difusion;

import Grafo.Grafo;
import java.util.*;

/**
 * Cadena de difusión más rápida: algoritmo de Dijkstra (estrategia Greedy).
 * En cada iteración se fija el nodo pendiente de menor tiempo conocido;
 * como los pesos son positivos, ese tiempo ya es definitivo.
 * Complejidad: O(V^2) con selección lineal del mínimo.
 */
public class CadenaDifusion {
    private static final int INF = Integer.MAX_VALUE;

    public static ResultadoDifusion calcular(Grafo g, int origen, int destino) {
        if (!g.existeVertice(origen)) {
            throw new IllegalArgumentException("El usuario origen no existe: " + origen);
        }
        if (!g.existeVertice(destino)) {
            throw new IllegalArgumentException("El usuario destino no existe: " + destino);
        }

        Map<Integer, Integer> dist = new HashMap<>();
        Map<Integer, Integer> pred = new HashMap<>();
        Set<Integer> visitados = new HashSet<>();
        Set<Integer> pendientes = new HashSet<>(g.getVertices());
        List<String> pasos = new ArrayList<>();

        for (int v : g.getVertices()) dist.put(v, INF);
        dist.put(origen, 0);
        pasos.add("Inicio: origen=" + origen + ", destino=" + destino + ", tiempo(origen)=0");

        while (!pendientes.isEmpty()) {
            int w = seleccionarMinimo(pendientes, dist);
            if (w == -1 || dist.get(w) == INF) {
                pasos.add("No quedan usuarios alcanzables. Fin del cálculo.");
                break;
            }
            pendientes.remove(w);
            visitados.add(w);
            pasos.add("Se fija el usuario " + w + " con tiempo mínimo " + dist.get(w) + " min");

            if (w == destino) {
                pasos.add("Se alcanzó el destino " + destino + ". Su tiempo ya es definitivo.");
                break;
            }

            // Relajación de los vínculos salientes de w
            for (Map.Entry<Integer, Integer> e : g.getAdyacentes(w).entrySet()) {
                int p = e.getKey();
                if (visitados.contains(p)) continue;
                int nuevo = dist.get(w) + e.getValue();
                if (nuevo < dist.get(p)) {
                    String antes = dist.get(p) == INF ? "∞" : String.valueOf(dist.get(p));
                    dist.put(p, nuevo);
                    pred.put(p, w);
                    pasos.add("  Mejora para " + p + ": " + antes + " -> " + nuevo
                            + " (pasando por " + w + ")");
                }
            }
        }

        if (dist.get(destino) == INF) {
            pasos.add("Resultado: el destino " + destino + " no es alcanzable.");
            return new ResultadoDifusion(false, new ArrayList<>(), -1, pasos);
        }

        List<Integer> cadena = reconstruirCamino(pred, origen, destino);
        return new ResultadoDifusion(true, cadena, dist.get(destino), pasos);
    }

    /** Devuelve el pendiente con menor distancia, o -1 si no hay pendientes. */
    private static int seleccionarMinimo(Set<Integer> pendientes, Map<Integer, Integer> dist) {
        int mejor = -1;
        int mejorDist = INF;
        boolean hay = false;
        for (int v : pendientes) {
            int d = dist.get(v);
            if (!hay || d < mejorDist || (d == mejorDist && v < mejor)) { // desempate determinista
                mejor = v;
                mejorDist = d;
                hay = true;
            }
        }
        return hay ? mejor : -1;
    }

    /** Recorre pred desde destino hasta origen y da vuelta la lista. */
    private static List<Integer> reconstruirCamino(Map<Integer, Integer> pred, int origen, int destino) {
        LinkedList<Integer> camino = new LinkedList<>();
        int actual = destino;
        camino.addFirst(actual);
        while (actual != origen) {
            actual = pred.get(actual);
            camino.addFirst(actual);
        }
        return new ArrayList<>(camino);
    }
}