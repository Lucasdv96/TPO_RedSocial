import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Grafo.Grafo;
import Difusion.CadenaDifusion;
import Difusion.ResultadoDifusion;


public class Main {
    private static void mostrar (Publicacion[]arr){
        if (arr.length == 0) {
            System.out.println("(sin publicaciones)");
            return;
        }
        for (Publicacion p : arr) {
            System.out.println(p);
        }
    }

    public static void main(String[] args) {

        // ================================================================
        //  PUNTO 1 — Muro de publicaciones (MergeSort)
        // ================================================================
        Muro muro = new Muro();
        LocalDateTime base = LocalDateTime.of(2026, 10, 20, 12, 0);

        muro.agregar(new Publicacion(1, "Ana", base.plusHours(1), 10, 2));
        muro.agregar(new Publicacion(2, "Luis", base.plusHours(5), 3, 1));
        muro.agregar(new Publicacion(3, "Sofi", base.plusHours(3), 10, 2)); // misma relevancia que id 1
        muro.agregar(new Publicacion(4, "Mati", base.plusHours(3), 1, 0)); // misma fecha que id 3
        muro.agregar(new Publicacion(5, "Caro", base.plusHours(2), 7, 5));

        System.out.println("=== Por fecha (más reciente primero) ===");
        mostrar(muro.consultarPorFecha());

        System.out.println("\n=== Por relevancia (más relevante primero) ===");
        mostrar(muro.consultarPorRelevancia());

        // Casos borde
        System.out.println("\n=== Muro vacío ===");
        mostrar(new Muro().consultarPorFecha());

        Muro unico = new Muro();
        unico.agregar(new Publicacion(9, "Solo", base, 0, 0));
        System.out.println("\n=== Una sola publicación ===");
        mostrar(unico.consultarPorRelevancia());

        // ================================================================
        //  PUNTO 2 — Cadena de difusión más rápida (Dijkstra)
        // ================================================================
        System.out.println("\n========================================");
        System.out.println("  CADENA DE DIFUSIÓN MÁS RÁPIDA");
        System.out.println("========================================");

        /*
         * Red de usuarios (grafo dirigido con tiempos en minutos):
         *
         *   1 --5--> 2 --2--> 5
         *   |        |        |
         *   3        1        3
         *   v        v        v
         *   3 --1--> 4 --6--> 6
         *
         *   7 (aislado, sin vínculos)
         */
        Grafo red = new Grafo();
        red.agregarArista(1, 2, 5);   // usuario 1 -> 2 en 5 min
        red.agregarArista(1, 3, 3);   // usuario 1 -> 3 en 3 min
        red.agregarArista(2, 4, 1);   // usuario 2 -> 4 en 1 min
        red.agregarArista(2, 5, 2);   // usuario 2 -> 5 en 2 min
        red.agregarArista(3, 4, 1);   // usuario 3 -> 4 en 1 min
        red.agregarArista(4, 6, 6);   // usuario 4 -> 6 en 6 min
        red.agregarArista(5, 6, 3);   // usuario 5 -> 6 en 3 min
        red.agregarVertice(7);        // usuario 7 aislado

        // --- Caso 1: camino alcanzable (1 -> 6) ---
        System.out.println("\n--- Caso 1: Difusión de usuario 1 a usuario 6 ---");
        ResultadoDifusion r1 = CadenaDifusion.calcular(red, 1, 6);
        System.out.println(r1);
        // Camino óptimo esperado: 1 -> 2 -> 5 -> 6  (5+2+3 = 10 min)

        // --- Caso 2: destino no alcanzable (1 -> 7) ---
        System.out.println("--- Caso 2: Difusión de usuario 1 a usuario 7 (aislado) ---");
        ResultadoDifusion r2 = CadenaDifusion.calcular(red, 1, 7);
        System.out.println(r2);

        // --- Caso 3: origen = destino (1 -> 1) ---
        System.out.println("--- Caso 3: Difusión de usuario 1 a sí mismo ---");
        ResultadoDifusion r3 = CadenaDifusion.calcular(red, 1, 1);
        System.out.println(r3);



        // ================================================================
        //  PUNTO 3 — Seleccion de iniciativas para financiar
        // ================================================================


        List<Iniciativa> iniciativas = new ArrayList<>();
        iniciativas.add(new Iniciativa("Hackaton", 1, 10));
        iniciativas.add(new Iniciativa("Biblioteca", 4, 3));
        iniciativas.add(new Iniciativa("Tutorias", 5, 5));
        iniciativas.add(new Iniciativa("Laboratorio", 7, 8));

        MochilaIniciativas mochila = new MochilaIniciativas(iniciativas, 8);
        mochila.resolver();

        System.out.println("Matriz M (igual que la tabla del ejemplo de la clase 25/09):");
        mochila.mostrarMatriz();
        System.out.println();
        mochila.mostrarResultado();

        // ===== CASOS DE PRUEBA =====
        System.out.println("\n\n########## CASOS DE PRUEBA ##########");

        // Greedy por impacto/costo elige A (ratio 5) y obtiene 30
        List<Iniciativa> greedyFalla = new ArrayList<>();
        greedyFalla.add(new Iniciativa("A", 6, 30));
        greedyFalla.add(new Iniciativa("B", 5, 24));
        greedyFalla.add(new Iniciativa("C", 5, 24));
        probar("Greedy falla, PD acierta (esperado: B y C, impacto 48)", greedyFalla, 10);

        // Todas son mas caras que el presupuesto
        List<Iniciativa> ningunaEntra = new ArrayList<>();
        ningunaEntra.add(new Iniciativa("X", 50, 100));
        ningunaEntra.add(new Iniciativa("Y", 30, 60));
        probar("Ninguna entra (esperado: ninguna, impacto 0)", ningunaEntra, 20);

        // Sobra presupuesto para todas
        List<Iniciativa> todasEntran = new ArrayList<>();
        todasEntran.add(new Iniciativa("X", 2, 5));
        todasEntran.add(new Iniciativa("Y", 3, 7));
        probar("Todas entran (esperado: X e Y, impacto 12)", todasEntran, 100);

        // Una iniciativa con impacto 0 no se elige: gasta plata y no suma
        List<Iniciativa> impactoCero = new ArrayList<>();
        impactoCero.add(new Iniciativa("Util", 4, 10));
        impactoCero.add(new Iniciativa("Nula", 3, 0));
        probar("Impacto cero (esperado: solo Util, impacto 10)", impactoCero, 10);

        // Sin presupuesto
        List<Iniciativa> sinPlata = new ArrayList<>();
        sinPlata.add(new Iniciativa("X", 1, 5));
        probar("Presupuesto cero (esperado: ninguna, impacto 0)", sinPlata, 0);

        // Lista vacia
        probar("Sin iniciativas (esperado: ninguna, impacto 0)", new ArrayList<>(), 50);
    }

    private static void probar(String titulo, List<Iniciativa> lista, int presupuesto) {
        System.out.println("\n=== " + titulo + " ===");
        MochilaIniciativas mochila = new MochilaIniciativas(lista, presupuesto);
        mochila.resolver();
        mochila.mostrarResultado();
    }
}



