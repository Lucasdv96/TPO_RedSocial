package app;

import algoritmos.CadenaDifusion;
import algoritmos.MochilaIniciativas;
import algoritmos.SeleccionActividades;
import estructuras.Grafo;
import modelo.Actividad;
import modelo.Iniciativa;
import modelo.Publicacion;
import servicios.Muro;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/** Datos de ejemplo y casos de prueba predefinidos de las funcionalidades. */
public class CasosDePrueba {

    public static final int PRESUPUESTO_EJEMPLO = 8;

    // ---------- Datos de ejemplo ----------

    public static Muro muroEjemplo() {
        Muro muro = new Muro();
        LocalDateTime base = LocalDateTime.of(2026, 10, 20, 12, 0);
        muro.agregar(new Publicacion(1, "Ana", base.plusHours(1), 10, 2));
        muro.agregar(new Publicacion(2, "Luis", base.plusHours(5), 3, 1));
        muro.agregar(new Publicacion(3, "Sofi", base.plusHours(3), 10, 2)); // misma relevancia que id 1
        muro.agregar(new Publicacion(4, "Mati", base.plusHours(3), 1, 0)); // misma fecha que id 3
        muro.agregar(new Publicacion(5, "Caro", base.plusHours(2), 7, 5));
        return muro;
    }

    /*
     * Red de usuarios (grafo dirigido con tiempos en minutos):
     *
     *   1 --5--> 2 --2--> 5
     *   |        |        |
     *   3        1        3
     *   v        v        v
     *   3 --1--> 4 --6--> 6
     *
     *   7 (aislado, sin vinculos)
     */
    public static Grafo redEjemplo() {
        Grafo red = new Grafo();
        red.agregarArista(1, 2, 5);
        red.agregarArista(1, 3, 3);
        red.agregarArista(2, 4, 1);
        red.agregarArista(2, 5, 2);
        red.agregarArista(3, 4, 1);
        red.agregarArista(4, 6, 6);
        red.agregarArista(5, 6, 3);
        red.agregarVertice(7);
        return red;
    }

    public static List<Iniciativa> iniciativasEjemplo() {
        List<Iniciativa> iniciativas = new ArrayList<>();
        iniciativas.add(new Iniciativa("Hackaton", 1, 10));
        iniciativas.add(new Iniciativa("Biblioteca", 4, 3));
        iniciativas.add(new Iniciativa("Tutorias", 5, 5));
        iniciativas.add(new Iniciativa("Laboratorio", 7, 8));
        return iniciativas;
    }

    /*
     * Actividades de ejemplo. Greedy (terminar lo antes posible) elegiria B, C y D (interes 10);
     * la mejor opcion es A y D (interes 14), y D empieza justo cuando termina A.
     *
     *   A  08-12  interes 10
     *   B  08-10  interes 3
     *   C  10-12  interes 3
     *   E  11-13  interes 6
     *   D  12-14  interes 4
     */
    public static List<Actividad> actividadesEjemplo() {
        List<Actividad> lista = new ArrayList<>();
        lista.add(act("A", 8, 12, 10));
        lista.add(act("B", 8, 10, 3));
        lista.add(act("C", 10, 12, 3));
        lista.add(act("D", 12, 14, 4));
        lista.add(act("E", 11, 13, 6));
        return lista;
    }

    private static Actividad act(String id, int horaInicio, int horaFin, int interes) {
        return new Actividad(id, LocalTime.of(horaInicio, 0), LocalTime.of(horaFin, 0), interes);
    }

    // ---------- Casos de prueba ----------

    public static void ejecutarTodos() {
        probarMuro();
        probarDifusion();
        probarMochila();
        probarActividades();
    }

    public static void probarMuro() {
        titulo("PUNTO 1 - Muro de publicaciones (Merge Sort)");
        Muro muro = muroEjemplo();

        System.out.println("\n--- Por fecha (esperado: ids 2, 3, 4, 5, 1) ---");
        Menu.mostrarPublicaciones(muro.consultarPorFecha());

        System.out.println("\n--- Por relevancia (esperado: ids 3, 5, 1, 2, 4) ---");
        Menu.mostrarPublicaciones(muro.consultarPorRelevancia());

        System.out.println("\n--- Muro vacio (esperado: sin publicaciones) ---");
        Menu.mostrarPublicaciones(new Muro().consultarPorFecha());

        LocalDateTime base = LocalDateTime.of(2026, 10, 20, 12, 0);
        Muro unico = new Muro();
        unico.agregar(new Publicacion(9, "Solo", base, 0, 0));
        System.out.println("\n--- Una sola publicacion (esperado: id 9) ---");
        Menu.mostrarPublicaciones(unico.consultarPorRelevancia());

        Muro empate = new Muro();
        empate.agregar(new Publicacion(30, "C", base, 5, 5));
        empate.agregar(new Publicacion(10, "A", base, 5, 5));
        empate.agregar(new Publicacion(20, "B", base, 5, 5));
        System.out.println("\n--- Empate total de fecha y relevancia (esperado: ids 10, 20, 30) ---");
        Menu.mostrarPublicaciones(empate.consultarPorRelevancia());
    }

    public static void probarDifusion() {
        titulo("PUNTO 2 - Cadena de difusion mas rapida (Dijkstra)");
        Grafo red = redEjemplo();

        // Hay dos cadenas de 10 min (1->3->4->6 y 1->2->5->6): cualquiera de las dos es optima
        System.out.println("\n--- Caso 1: usuario 1 a usuario 6 (esperado: 10 min, por 1->3->4->6 o 1->2->5->6) ---");
        System.out.println(CadenaDifusion.calcular(red, 1, 6));

        System.out.println("--- Caso 2: usuario 1 a usuario 7, aislado (esperado: no alcanzable) ---");
        System.out.println(CadenaDifusion.calcular(red, 1, 7));

        System.out.println("--- Caso 3: usuario 1 a si mismo (esperado: cadena 1, 0 min) ---");
        System.out.println(CadenaDifusion.calcular(red, 1, 1));
    }

    public static void probarMochila() {
        titulo("PUNTO 3 - Seleccion de iniciativas (Programacion Dinamica)");

        MochilaIniciativas mochila = new MochilaIniciativas(iniciativasEjemplo(), PRESUPUESTO_EJEMPLO);
        mochila.resolver();
        System.out.println("\nMatriz M (igual que la tabla del ejemplo de la clase 25/09):");
        mochila.mostrarMatriz();
        System.out.println();
        mochila.mostrarResultado();

        // Greedy por impacto/costo elige A (ratio 5) y obtiene 30
        List<Iniciativa> greedyFalla = new ArrayList<>();
        greedyFalla.add(new Iniciativa("A", 6, 30));
        greedyFalla.add(new Iniciativa("B", 5, 24));
        greedyFalla.add(new Iniciativa("C", 5, 24));
        probar("Greedy falla, PD acierta (esperado: B y C, impacto 48)", greedyFalla, 10);

        List<Iniciativa> ningunaEntra = new ArrayList<>();
        ningunaEntra.add(new Iniciativa("X", 50, 100));
        ningunaEntra.add(new Iniciativa("Y", 30, 60));
        probar("Ninguna entra (esperado: ninguna, impacto 0)", ningunaEntra, 20);

        List<Iniciativa> todasEntran = new ArrayList<>();
        todasEntran.add(new Iniciativa("X", 2, 5));
        todasEntran.add(new Iniciativa("Y", 3, 7));
        probar("Todas entran (esperado: X e Y, impacto 12)", todasEntran, 100);

        List<Iniciativa> impactoCero = new ArrayList<>();
        impactoCero.add(new Iniciativa("Util", 4, 10));
        impactoCero.add(new Iniciativa("Nula", 3, 0));
        probar("Impacto cero (esperado: solo Util, impacto 10)", impactoCero, 10);

        List<Iniciativa> sinPlata = new ArrayList<>();
        sinPlata.add(new Iniciativa("X", 1, 5));
        probar("Presupuesto cero (esperado: ninguna, impacto 0)", sinPlata, 0);

        probar("Sin iniciativas (esperado: ninguna, impacto 0)", new ArrayList<>(), 50);
    }

    public static void probarActividades() {
        titulo("A3 - Seleccion de actividades compatibles (Programacion Dinamica)");

        SeleccionActividades ejemplo = new SeleccionActividades(actividadesEjemplo());
        ejemplo.resolver();
        System.out.println("\nTabla de programacion dinamica (esperado: A y D, interes 14):");
        ejemplo.mostrarTabla();
        System.out.println();
        ejemplo.mostrarResultado();

        List<Actividad> greedyFalla = new ArrayList<>();
        greedyFalla.add(act("Larga", 1, 10, 100));
        greedyFalla.add(act("C1", 1, 3, 10));
        greedyFalla.add(act("C2", 3, 6, 10));
        greedyFalla.add(act("C3", 6, 10, 10));
        probarActividades("Greedy falla, PD acierta (esperado: Larga, interes 100)", greedyFalla);

        List<Actividad> frontera = new ArrayList<>();
        frontera.add(act("X", 8, 10, 5));
        frontera.add(act("Y", 10, 12, 7));
        probarActividades("Una empieza cuando termina la otra (esperado: X e Y, interes 12)", frontera);

        List<Actividad> superpuestas = new ArrayList<>();
        superpuestas.add(act("P", 8, 12, 4));
        superpuestas.add(act("Q", 9, 11, 9));
        superpuestas.add(act("R", 10, 13, 6));
        probarActividades("Todas se superponen (esperado: solo Q, interes 9)", superpuestas);

        List<Actividad> unica = new ArrayList<>();
        unica.add(act("Sola", 9, 10, 4));
        probarActividades("Una sola actividad (esperado: Sola, interes 4)", unica);

        probarActividades("Sin actividades (esperado: ninguna, interes 0)", new ArrayList<>());

        List<Actividad> interesCero = new ArrayList<>();
        interesCero.add(act("Util", 8, 10, 5));
        interesCero.add(act("Nula", 10, 12, 0));
        probarActividades("Interes cero (esperado: solo Util, interes 5)", interesCero);

        List<Actividad> empate = new ArrayList<>();
        empate.add(act("N", 9, 11, 5));
        empate.add(act("M", 8, 10, 5));
        probarActividades("Empate de interes total (esperado: M, la que termina antes, interes 5)", empate);
    }

    private static void probarActividades(String titulo, List<Actividad> lista) {
        System.out.println("\n--- " + titulo + " ---");
        SeleccionActividades seleccion = new SeleccionActividades(lista);
        seleccion.resolver();
        seleccion.mostrarResultado();
    }

    private static void probar(String titulo, List<Iniciativa> lista, int presupuesto) {
        System.out.println("\n--- " + titulo + " ---");
        MochilaIniciativas mochila = new MochilaIniciativas(lista, presupuesto);
        mochila.resolver();
        mochila.mostrarResultado();
    }

    private static void titulo(String texto) {
        System.out.println("\n========================================");
        System.out.println("  " + texto);
        System.out.println("========================================");
    }
}