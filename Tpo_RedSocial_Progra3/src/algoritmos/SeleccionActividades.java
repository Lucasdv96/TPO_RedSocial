package algoritmos;

import modelo.Actividad;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SeleccionActividades {

    // Fin mas temprano primero. Desempates: inicio mas temprano y luego id (resultado determinista).
    private static final Comparator<Actividad> POR_FIN = (a, b) -> {
        int c = a.getFin().compareTo(b.getFin());
        if (c != 0) return c;
        c = a.getInicio().compareTo(b.getInicio());
        if (c != 0) return c;
        return a.getId().compareTo(b.getId());
    };

    private final Actividad[] actividadesOrdenadas;
    private final int n;
    private int[] previa;
    private int[] mejorInteres;
    private int interesTotal;
    private List<Actividad> seleccionadas;

    public SeleccionActividades(List<Actividad> actividades) {
        if (actividades == null) {
            throw new IllegalArgumentException("La lista de actividades no puede ser nula");
        }
        this.n = actividades.size();
        this.actividadesOrdenadas = actividades.toArray(new Actividad[0]);
        MergeSort.ordenar(actividadesOrdenadas, POR_FIN);
    }

    public void resolver() {
        /*
        Calcula la columna previa.
        Llena la tabla y guarda el interes.
        Reconstruye cuales actividades se eligieron.
         */
        calcularPrevias();
        interesTotal = llenarTabla();
        reconstruir();
    }

    private void calcularPrevias() {
        /*
        Calcula, para cada actividad i (de 1 a n), cuantas actividades anteriores
        terminan a la hora de inicio de i o antes, y lo guarda en previa[i].

        Pre: actividadesOrdenadas ya esta ordenada por hora de fin.
        Post: previa tiene n + 1 posiciones. Para todo i entre 1 y n,
              previa[i] esta entre 0 e i - 1. previa[0] no se usa.
         */

        previa = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            previa[i] = buscarPrevia(i);
        }
    }

    private int buscarPrevia(int i) {
        /*
        Calcula cuantas actividades anteriores a la i son compatibles con ella,
        es decir, terminan a la hora de inicio de i o antes.

        Pre: actividadesOrdenadas debe estar ordenado por hora de fin.
        Post: devuelve k, la cantidad de actividades compatibles.
              Las compatibles son exactamente las k primeras de la lista.
         */
        LocalTime inicio = actividadesOrdenadas[i - 1].getInicio();
        int desde = 0;
        int hasta = i - 1;
        while (desde < hasta) {
            int medio = (desde + hasta + 1) / 2;
            if (actividadesOrdenadas[medio - 1].getFin().compareTo(inicio) <= 0) {
                desde = medio;
            } else {
                hasta = medio - 1;
            }
        }
        return desde;
    }

    private int llenarTabla() {
        /*
         Llena la tabla mejorInteres y devuelve el mayor interés total posible.

         Pre:  actividadesOrdenadas está ordenado por hora de fin y previa ya fue
               calculada (calcularPrevias se ejecutó antes).
         Post: mejorInteres tiene n + 1 posiciones con mejorInteres[0] = 0.
               Para 1 <= i <= n, mejorInteres[i] es el mayor interés que se logra
               con las primeras i actividades sin superposiciones.
               Devuelve mejorInteres[n].
         */

        mejorInteres = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            int sinTomar = mejorInteres[i - 1];
            int tomando = actividadesOrdenadas[i - 1].getInteres() + mejorInteres[previa[i]];
            mejorInteres[i] = Math.max(sinTomar, tomando);
        }
        return mejorInteres[n];
    }

    /**
     * Recorre la tabla hacia atras. Si dp[i] != dp[i-1], la actividad i fue necesaria:
     * se elige y se salta a previa[i]. Si son iguales, se descarta.
     * En empate de interes no se incluye la actividad (se prefiere la que termina antes).
     */
    private void reconstruir() {
        /*
         Determina cuales actividades forman la selección de mayor interes y las
         guarda en seleccionadas, en orden cronologico.

         Pre:  actividadesOrdenadas esta ordenado por hora de fin, y previa y
               mejorInteres ya fueron calculadas (calcularPrevias y llenarTabla
               se ejecutaron antes).
         Post: seleccionadas contiene actividades que no se superponen entre si,
               ordenadas cronologicamente, y la suma de sus intereses es igual a
               mejorInteres[n].
               Si dos opciones empatan en interes, no se incluye la actividad
               que no aporta (se prefiere la que termina antes).
         */
        seleccionadas = new ArrayList<>();
        int i = n;
        while (i > 0) {
            if (mejorInteres[i] != mejorInteres[i - 1]) {
                seleccionadas.add(actividadesOrdenadas[i - 1]);
                i = previa[i];
            } else {
                i--;
            }
        }
        Collections.reverse(seleccionadas);
    }

    public int getInteresTotal() {
        return interesTotal;
    }

    public List<Actividad> getSeleccionadas() {
        return seleccionadas;
    }

    public void mostrarResultado() {
        System.out.println("=== Seleccion de actividades compatibles ===");
        if (seleccionadas.isEmpty()) {
            System.out.println("No hay actividades para seleccionar (o ninguna aporta interes).");
        } else {
            System.out.println("Actividades elegidas (en orden cronologico):");
            for (Actividad a : seleccionadas) {
                System.out.println("  - " + a);
            }
        }
        System.out.println("Interes total obtenido: " + interesTotal);
    }

    /** Imprime la tabla de programacion dinamica (actividades ordenadas por hora de fin). */
    public void mostrarTabla() {
        System.out.println(String.format("%3s  %-12s %-13s %7s %5s %6s", "i", "Actividad", "Horario", "Interes", "p(i)", "dp[i]"));
        System.out.println(String.format("%3d  %-12s %-13s %7s %5s %6d", 0, "-", "-", "-", "-", mejorInteres[0]));
        for (int i = 1; i <= n; i++) {
            Actividad a = actividadesOrdenadas[i - 1];
            System.out.println(String.format("%3d  %-12s %-13s %7d %5d %6d", i, a.getId(),
                    a.getInicio() + "-" + a.getFin(), a.getInteres(), previa[i], mejorInteres[i]));
        }
    }
}
