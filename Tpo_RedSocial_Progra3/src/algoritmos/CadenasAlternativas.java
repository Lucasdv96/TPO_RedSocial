package algoritmos;

import estructuras.Grafo;
import modelo.Cadena;
import modelo.ResultadoCadenas;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Cadenas alternativas de difusion: Backtracking.
 * Se arma la cadena de a un usuario por vez (busqueda en profundidad). Antes de avanzar
 * se descartan las extensiones que violan alguna condicion (poda) y, al volver de la
 * recursion, se deshace el ultimo paso para probar el siguiente vecino.
 * Complejidad en el peor caso: O(V!), porque se exploran todos los caminos simples.
 */
public class CadenasAlternativas {

    // Orden de las cadenas: menor tiempo, luego menos intermediarios, luego usuarios en orden
    private static final Comparator<Cadena> POR_TIEMPO = (a, b) -> {
        int c = Integer.compare(a.getTiempoTotal(), b.getTiempoTotal());
        if (c != 0) return c;
        c = Integer.compare(a.getIntermediarios(), b.getIntermediarios());
        if (c != 0) return c;
        List<Integer> ua = a.getUsuarios();
        List<Integer> ub = b.getUsuarios();
        for (int i = 0; i < Math.min(ua.size(), ub.size()); i++) {
            c = Integer.compare(ua.get(i), ub.get(i));
            if (c != 0) return c;
        }
        return Integer.compare(ua.size(), ub.size());
    };

    private final Grafo red;
    private final int destino;
    private final int tiempoMaximo;
    private final int maxIntermediarios;
    private final Set<Integer> excluidos;

    private final List<Integer> cadena = new ArrayList<>();
    private final Set<Integer> enCadena = new HashSet<>();
    private final List<Cadena> soluciones = new ArrayList<>();
    private int llamadas = 0;
    private int podas = 0;

    private CadenasAlternativas(Grafo red, int destino, int tiempoMaximo, int maxIntermediarios, Set<Integer> excluidos) {
        this.red = red;
        this.destino = destino;
        this.tiempoMaximo = tiempoMaximo;
        this.maxIntermediarios = maxIntermediarios;
        this.excluidos = excluidos;
    }

    public static ResultadoCadenas buscar(Grafo red, int origen, int destino, int tiempoMaximo,
                                          int maxIntermediarios, Set<Integer> excluidos) {
        if (!red.existeVertice(origen)) {
            throw new IllegalArgumentException("El usuario origen no existe: " + origen);
        }
        if (!red.existeVertice(destino)) {
            throw new IllegalArgumentException("El usuario destino no existe: " + destino);
        }
        if (tiempoMaximo < 0) {
            throw new IllegalArgumentException("El tiempo maximo no puede ser negativo: " + tiempoMaximo);
        }
        if (maxIntermediarios < 0) {
            throw new IllegalArgumentException("El limite de intermediarios no puede ser negativo: " + maxIntermediarios);
        }
        Set<Integer> excl = (excluidos == null) ? new HashSet<>() : new HashSet<>(excluidos);
        List<Cadena> vacia = new ArrayList<>();

        if (excl.contains(origen) || excl.contains(destino)) {
            return new ResultadoCadenas(vacia, vacia,
                    "El origen o el destino esta excluido: no puede haber cadenas.", 0, 0);
        }

        CadenasAlternativas busqueda = new CadenasAlternativas(red, destino, tiempoMaximo, maxIntermediarios, excl);
        if (origen == destino) {
            busqueda.cadena.add(origen);
            busqueda.soluciones.add(new Cadena(busqueda.cadena, 0));
        } else {
            busqueda.cadena.add(origen);
            busqueda.enCadena.add(origen);
            busqueda.explorar(origen, 0);
        }
        return busqueda.armarResultado();
    }

    private void explorar(int actual, int tiempoAcumulado) {
        llamadas++;
        for (Map.Entry<Integer, Integer> vinculo : red.getAdyacentes(actual).entrySet()) {
            int vecino = vinculo.getKey();
            int nuevoTiempo = tiempoAcumulado + vinculo.getValue();

            // PODA 1: usuario excluido o ya presente en la cadena (no se repiten usuarios)
            if (excluidos.contains(vecino) || enCadena.contains(vecino)) {
                podas++;
                continue;
            }
            // PODA 2: se supera el tiempo maximo. Como los tiempos son positivos,
            // ninguna extension de esta cadena puede volver a quedar dentro del limite.
            if (nuevoTiempo > tiempoMaximo) {
                podas++;
                continue;
            }
            // SOLUCION: se llego al destino respetando todas las condiciones
            if (vecino == destino) {
                cadena.add(vecino);
                soluciones.add(new Cadena(cadena, nuevoTiempo));
                cadena.remove(cadena.size() - 1);
                continue;
            }
            // PODA 3: el vecino seria un intermediario mas y se pasaria del limite
            // (la cadena actual ya tiene cadena.size() - 1 intermediarios)
            if (cadena.size() > maxIntermediarios) {
                podas++;
                continue;
            }
            // PODA 4: el tiempo ya llego al maximo y falta al menos un vinculo (positivo) hasta el destino
            if (nuevoTiempo >= tiempoMaximo) {
                podas++;
                continue;
            }

            // AVANZAR
            cadena.add(vecino);
            enCadena.add(vecino);
            explorar(vecino, nuevoTiempo);
            // RETROCEDER
            cadena.remove(cadena.size() - 1);
            enCadena.remove(vecino);
        }
    }

    private ResultadoCadenas armarResultado() {
        Cadena[] arr = soluciones.toArray(new Cadena[0]);
        MergeSort.ordenar(arr, POR_TIEMPO);

        List<Cadena> ordenadas = new ArrayList<>();
        List<Cadena> mejores = new ArrayList<>();
        for (Cadena c : arr) {
            ordenadas.add(c);
        }
        if (arr.length > 0) {
            Cadena primera = arr[0];
            for (Cadena c : arr) {
                if (c.getTiempoTotal() == primera.getTiempoTotal()
                        && c.getIntermediarios() == primera.getIntermediarios()) {
                    mejores.add(c);
                }
            }
        }
        return new ResultadoCadenas(ordenadas, mejores, null, llamadas, podas);
    }
}