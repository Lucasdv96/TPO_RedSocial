package app;

import algoritmos.CadenaDifusion;
import algoritmos.MochilaIniciativas;
import estructuras.Grafo;
import modelo.Iniciativa;
import modelo.Publicacion;
import servicios.Muro;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Datos de ejemplo y casos de prueba predefinidos de las tres funcionalidades. */
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

    // ---------- Casos de prueba ----------

    public static void ejecutarTodos() {
        probarMuro();
        probarDifusion();
        probarMochila();
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