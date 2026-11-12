package app;

import algoritmos.CadenaDifusion;
import algoritmos.CadenasAlternativas;
import algoritmos.FormacionEquipos;
import algoritmos.MochilaIniciativas;
import estructuras.Grafo;
import modelo.Estudiante;
import modelo.Iniciativa;
import modelo.Publicacion;
import servicios.Muro;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static final int TAMANIO_EQUIPO_EJEMPLO = 3;

    public static List<Estudiante> estudiantesEjemplo() {
        List<Estudiante> estudiantes = new ArrayList<>();
        estudiantes.add(new Estudiante("Ana", Arrays.asList("Java", "SQL")));
        estudiantes.add(new Estudiante("Beto", Arrays.asList("Diseno")));
        estudiantes.add(new Estudiante("Caro", Arrays.asList("Java", "Testing")));
        estudiantes.add(new Estudiante("Dani", Arrays.asList("SQL", "Diseno")));
        estudiantes.add(new Estudiante("Eze", Arrays.asList("Testing")));
        estudiantes.add(new Estudiante("Flor", Arrays.asList("Diseno", "Testing")));
        return estudiantes;
    }

    public static List<String> habilidadesEjemplo() {
        return Arrays.asList("Java", "SQL", "Diseno", "Testing");
    }

    public static List<String> obligatoriosEjemplo() {
        return Arrays.asList("Ana");
    }

    public static List<String[]> incompatiblesEjemplo() {
        List<String[]> pares = new ArrayList<>();
        pares.add(new String[]{"Ana", "Flor"});
        return pares;
    }

    // ---------- Casos de prueba ----------

    public static void ejecutarTodos() {
        probarMuro();
        probarDifusion();
        probarMochila();
        probarCadenasAlternativas();
        probarEquipos();
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
    public static void probarCadenasAlternativas() {
        titulo("B3 - Cadenas alternativas de difusion (Backtracking)");
        Grafo red = redEjemplo();

        System.out.println("\n--- Caso 1: 1 a 6, max 15 min, hasta 3 intermediarios, sin excluidos ---");
        System.out.println("    (esperado: 3 cadenas; mejores empatadas 1->2->5->6 y 1->3->4->6, 10 min)");
        System.out.println(CadenasAlternativas.buscar(red, 1, 6, 15, 3, conjunto()));

        System.out.println("--- Caso 2: igual que el caso 1, excluyendo al usuario 3 ---");
        System.out.println("    (esperado: 2 cadenas; mejor 1->2->5->6, 10 min)");
        System.out.println(CadenasAlternativas.buscar(red, 1, 6, 15, 3, conjunto(3)));

        System.out.println("--- Caso 3: tiempo maximo exacto de 10 min (el limite es inclusivo) ---");
        System.out.println("    (esperado: 2 cadenas de 10 min; se descarta la de 12)");
        System.out.println(CadenasAlternativas.buscar(red, 1, 6, 10, 3, conjunto()));

        System.out.println("--- Caso 4: tiempo maximo de 9 min (esperado: ninguna cadena) ---");
        System.out.println(CadenasAlternativas.buscar(red, 1, 6, 9, 3, conjunto()));

        System.out.println("--- Caso 5: 1 a 4 con 0 intermediarios (esperado: ninguna, no hay vinculo directo) ---");
        System.out.println(CadenasAlternativas.buscar(red, 1, 4, 20, 0, conjunto()));

        System.out.println("--- Caso 6: 1 a 7, usuario aislado (esperado: ninguna cadena) ---");
        System.out.println(CadenasAlternativas.buscar(red, 1, 7, 100, 5, conjunto()));

        System.out.println("--- Caso 7: origen excluido (esperado: aviso, no se busca) ---");
        System.out.println(CadenasAlternativas.buscar(red, 1, 6, 15, 3, conjunto(1)));

        System.out.println("--- Caso 8: origen igual al destino (esperado: cadena 1, 0 min) ---");
        System.out.println(CadenasAlternativas.buscar(red, 1, 1, 15, 3, conjunto()));

        /*
         * Red donde el camino mas rapido tiene muchos intermediarios:
         *   1 --1--> 2 --1--> 3 --1--> 4      y      1 --10--> 4 (directo)
         * Dijkstra elige 1->2->3->4 (3 min, 2 intermediarios).
         */
        Grafo intermediarios = new Grafo();
        intermediarios.agregarArista(1, 2, 1);
        intermediarios.agregarArista(2, 3, 1);
        intermediarios.agregarArista(3, 4, 1);
        intermediarios.agregarArista(1, 4, 10);

        System.out.println("--- Caso 9: 1 a 4 con hasta 1 intermediario ---");
        System.out.println("    (Dijkstra da " + CadenaDifusion.calcular(intermediarios, 1, 4).getCadena()
                + "; esperado en B3: solo 1->4, 10 min)");
        System.out.println(CadenasAlternativas.buscar(intermediarios, 1, 4, 20, 1, conjunto()));

        /*
         * Red con ciclo 1 -> 2 -> 3 -> 1: sin el control de repetidos, la busqueda no terminaria.
         *   1 --1--> 2 --1--> 3 --1--> 4,   3 --1--> 1,   2 --5--> 4
         */
        Grafo ciclo = new Grafo();
        ciclo.agregarArista(1, 2, 1);
        ciclo.agregarArista(2, 3, 1);
        ciclo.agregarArista(3, 1, 1);
        ciclo.agregarArista(3, 4, 1);
        ciclo.agregarArista(2, 4, 5);

        System.out.println("--- Caso 10: red con ciclo, 1 a 4 (esperado: 1->2->3->4 de 3 min y 1->2->4 de 6 min) ---");
        System.out.println(CadenasAlternativas.buscar(ciclo, 1, 4, 20, 5, conjunto()));
    }

    private static Set<Integer> conjunto(int... usuarios) {
        Set<Integer> s = new HashSet<>();
        for (int u : usuarios) {
            s.add(u);
        }
        return s;
    }

    public static void probarEquipos() {
        titulo("B1 - Formacion de equipos con restricciones (Backtracking)");

        // Ana es obligatoria y aporta Java y SQL; faltan Diseno y Testing. Flor no puede estar con Ana.
        System.out.println("\n--- Ejemplo (esperado: 4 equipos: Ana-Beto-Caro, Ana-Beto-Eze, Ana-Caro-Dani, Ana-Dani-Eze) ---");
        FormacionEquipos equipos = new FormacionEquipos(estudiantesEjemplo(), TAMANIO_EQUIPO_EJEMPLO,
                habilidadesEjemplo(), obligatoriosEjemplo(), incompatiblesEjemplo());
        equipos.resolver();
        equipos.mostrarResultado();

        // Sin la incompatibilidad, Flor puede entrar con Ana: aparecen 4 equipos mas
        probarEquipo("Sin pares incompatibles (esperado: 8 equipos, se suman los 4 con Flor)",
                estudiantesEjemplo(), 3, habilidadesEjemplo(), obligatoriosEjemplo(), new ArrayList<>());

        // Sin requisitos: todas las combinaciones de 4 tomadas de a 2 = 6, ninguna repetida
        List<Estudiante> cuatro = new ArrayList<>(estudiantesEjemplo().subList(0, 4));
        probarEquipo("Sin repetidos: 4 estudiantes, equipos de 2, sin requisitos (esperado: 6 equipos)",
                cuatro, 2, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        probarEquipo("Habilidad que nadie tiene (esperado: ningun equipo)",
                estudiantesEjemplo(), 3, Arrays.asList("Java", "Python"), new ArrayList<>(), new ArrayList<>());

        List<String[]> obligatoriosPeleados = new ArrayList<>();
        obligatoriosPeleados.add(new String[]{"Ana", "Beto"});
        probarEquipo("Dos obligatorios incompatibles entre si (esperado: ningun equipo)",
                estudiantesEjemplo(), 3, habilidadesEjemplo(), Arrays.asList("Ana", "Beto"), obligatoriosPeleados);

        probarEquipo("Equipo mas grande que la cantidad de estudiantes (esperado: ningun equipo)",
                cuatro, 5, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        probarEquipo("Sin estudiantes (esperado: ningun equipo)",
                new ArrayList<>(), 2, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    private static void probarEquipo(String titulo, List<Estudiante> estudiantes, int tamanio,
                                     List<String> habilidades, List<String> obligatorios, List<String[]> incompatibles) {
        System.out.println("\n--- " + titulo + " ---");
        FormacionEquipos equipos = new FormacionEquipos(estudiantes, tamanio, habilidades, obligatorios, incompatibles);
        equipos.resolver();
        equipos.mostrarResultado();
    }

    private static void titulo(String texto) {
        System.out.println("\n========================================");
        System.out.println("  " + texto);
        System.out.println("========================================");
    }
}