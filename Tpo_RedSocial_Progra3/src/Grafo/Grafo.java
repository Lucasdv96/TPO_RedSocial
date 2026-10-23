package Grafo;

import java.util.*;

/** Grafo dirigido con pesos enteros positivos (tiempo en minutos). */
public class Grafo {

    private final Map<Integer, Map<Integer, Integer>> adyacencias = new HashMap<>();

    public void agregarVertice(int v) {
        adyacencias.putIfAbsent(v, new HashMap<>());
    }

    public void agregarArista(int origen, int destino, int peso) {
        if (peso <= 0) {
            throw new IllegalArgumentException("El tiempo del vínculo debe ser positivo: " + peso);
        }
        agregarVertice(origen);
        agregarVertice(destino);
        adyacencias.get(origen).put(destino, peso); // dirigido: solo origen -> destino
    }

    public boolean existeVertice(int v) {
        return adyacencias.containsKey(v);
    }

    public boolean existeArista(int u, int v) {
        return existeVertice(u) && adyacencias.get(u).containsKey(v);
    }

    public int getPeso(int u, int v) {
        if (!existeArista(u, v)) {
            throw new NoSuchElementException("No existe la arista " + u + " -> " + v);
        }
        return adyacencias.get(u).get(v);
    }

    public Set<Integer> getVertices() {
        return Collections.unmodifiableSet(adyacencias.keySet());
    }

    public Map<Integer, Integer> getAdyacentes(int v) {
        if (!existeVertice(v)) return Collections.emptyMap();
        return Collections.unmodifiableMap(adyacencias.get(v));
    }
}