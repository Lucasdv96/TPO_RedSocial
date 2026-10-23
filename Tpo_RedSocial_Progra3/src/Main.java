import java.time.LocalDateTime;
import Grafo.Grafo;
import Difusion.CadenaDifusion;
import Difusion.ResultadoDifusion;


public class Main {

    public static void main(String[] args) {

        // ================================================================
        //  PUNTO 1 — Muro de publicaciones (MergeSort)
        // ================================================================
        Muro muro = new Muro();
        LocalDateTime base = LocalDateTime.of(2026, 10, 20, 12, 0);

        muro.agregar(new Publicacion(1, "Ana",   base.plusHours(1), 10, 2));
        muro.agregar(new Publicacion(2, "Luis",  base.plusHours(5),  3, 1));
        muro.agregar(new Publicacion(3, "Sofi",  base.plusHours(3), 10, 2)); // misma relevancia que id 1
        muro.agregar(new Publicacion(4, "Mati",  base.plusHours(3),  1, 0)); // misma fecha que id 3
        muro.agregar(new Publicacion(5, "Caro",  base.plusHours(2),  7, 5));

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
    }

    private static void mostrar(Publicacion[] arr) {
        if (arr.length == 0) {
            System.out.println("(sin publicaciones)");
            return;
        }
        for (Publicacion p : arr) {
            System.out.println(p);
        }
    }
}

