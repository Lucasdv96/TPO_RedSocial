package app;

import algoritmos.CadenaDifusion;
import algoritmos.CadenasAlternativas;
import algoritmos.MochilaIniciativas;
import estructuras.Grafo;
import modelo.Iniciativa;
import modelo.Publicacion;
import modelo.ResultadoCadenas;
import modelo.ResultadoDifusion;
import servicios.Muro;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/** Menu por consola para ejecutar las funcionalidades con datos de ejemplo o desde archivos. */
public class Menu {

    private static final String RUTA_PUBLICACIONES = "datos/publicaciones.txt";
    private static final String RUTA_RED = "datos/red.txt";
    private static final String RUTA_INICIATIVAS = "datos/iniciativas.txt";
    private static final int MAX_PRESUPUESTO_MATRIZ = 20;

    private final Scanner entrada;

    public Menu(Scanner entrada) {
        this.entrada = entrada;
    }

    public void iniciar() {
        try {
            int opcion;
            do {
                System.out.println("\n===== RED SOCIAL UNIVERSITARIA =====");
                System.out.println("1. Muro de publicaciones (Merge Sort)");
                System.out.println("2. Cadena de difusion mas rapida (Dijkstra)");
                System.out.println("3. Seleccion de iniciativas a financiar (Mochila)");
                System.out.println("4. Ejecutar casos de prueba predefinidos");
                System.out.println("5. Cadenas alternativas de difusion (Backtracking)");
                System.out.println("0. Salir");
                opcion = leerEntero("Opcion: ");

                switch (opcion) {
                    case 1: opcionMuro(); break;
                    case 2: opcionDifusion(); break;
                    case 3: opcionIniciativas(); break;
                    case 4: CasosDePrueba.ejecutarTodos(); break;
                    case 5: opcionCadenasAlternativas(); break;
                    case 0: System.out.println("Hasta luego."); break;
                    default: System.out.println("Opcion invalida.");
                }
            } while (opcion != 0);
        } catch (NoSuchElementException e) {
            System.out.println("\nFin de la entrada. Hasta luego.");
        }
    }

    // ---------- Punto 1 ----------

    private void opcionMuro() {
        Muro muro;
        if (usarArchivo()) {
            try {
                muro = LectorArchivos.leerMuro(leerRuta(RUTA_PUBLICACIONES));
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("No se pudo cargar el archivo: " + e.getMessage());
                return;
            }
        } else {
            muro = CasosDePrueba.muroEjemplo();
        }

        System.out.println("\n=== Por fecha (mas reciente primero) ===");
        mostrarPublicaciones(muro.consultarPorFecha());
        System.out.println("\n=== Por relevancia (mas relevante primero) ===");
        mostrarPublicaciones(muro.consultarPorRelevancia());
    }

    // ---------- Punto 2 ----------

    private void opcionDifusion() {
        Grafo red = cargarRed();
        if (red == null) {
            return;
        }

        System.out.println("\nUsuarios de la red: " + new TreeSet<>(red.getVertices()));
        boolean otra;
        do {
            int origen = leerEntero("Usuario origen: ");
            int destino = leerEntero("Usuario destino: ");
            try {
                ResultadoDifusion resultado = CadenaDifusion.calcular(red, origen, destino);
                System.out.println();
                System.out.println(resultado);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
            otra = confirmar("Otra consulta con la misma red? (s/n): ");
        } while (otra);
    }

    // Carga la red de ejemplo o desde archivo (la usan los puntos 2 y B3). Devuelve null si falla.
    private Grafo cargarRed() {
        if (usarArchivo()) {
            try {
                return LectorArchivos.leerRed(leerRuta(RUTA_RED));
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("No se pudo cargar el archivo: " + e.getMessage());
                return null;
            }
        }
        return CasosDePrueba.redEjemplo();
    }

    // ---------- B3: cadenas alternativas ----------

    private void opcionCadenasAlternativas() {
        Grafo red = cargarRed();
        if (red == null) {
            return;
        }

        System.out.println("\nUsuarios de la red: " + new TreeSet<>(red.getVertices()));
        boolean otra;
        do {
            int origen = leerEntero("Usuario origen: ");
            int destino = leerEntero("Usuario destino: ");
            int tiempoMaximo = leerEntero("Tiempo maximo (min): ");
            int maxIntermediarios = leerEntero("Limite de intermediarios: ");
            Set<Integer> excluidos = leerExcluidos();
            try {
                ResultadoCadenas resultado = CadenasAlternativas.buscar(
                        red, origen, destino, tiempoMaximo, maxIntermediarios, excluidos);
                System.out.println();
                System.out.println(resultado);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
            otra = confirmar("Otra consulta con la misma red? (s/n): ");
        } while (otra);
    }

    private Set<Integer> leerExcluidos() {
        while (true) {
            System.out.print("Usuarios excluidos separados por coma (Enter si ninguno): ");
            String texto = entrada.nextLine().trim();
            Set<Integer> excluidos = new HashSet<>();
            if (texto.isEmpty()) {
                return excluidos;
            }
            try {
                for (String parte : texto.split(",")) {
                    excluidos.add(Integer.parseInt(parte.trim()));
                }
                return excluidos;
            } catch (NumberFormatException e) {
                System.out.println("Ingresa numeros enteros separados por coma, por ejemplo: 3,5");
            }
        }
    }

    // ---------- Punto 3 ----------

    private void opcionIniciativas() {
        List<Iniciativa> iniciativas;
        int presupuesto;
        if (usarArchivo()) {
            try {
                LectorArchivos.DatosMochila datos = LectorArchivos.leerIniciativas(leerRuta(RUTA_INICIATIVAS));
                iniciativas = datos.getIniciativas();
                presupuesto = datos.getPresupuesto();
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("No se pudo cargar el archivo: " + e.getMessage());
                return;
            }
        } else {
            iniciativas = CasosDePrueba.iniciativasEjemplo();
            presupuesto = CasosDePrueba.PRESUPUESTO_EJEMPLO;
        }

        MochilaIniciativas mochila = new MochilaIniciativas(iniciativas, presupuesto);
        mochila.resolver();
        System.out.println();
        if (presupuesto <= MAX_PRESUPUESTO_MATRIZ) {
            System.out.println("Matriz M:");
            mochila.mostrarMatriz();
            System.out.println();
        } else {
            System.out.println("(La matriz no se muestra porque el presupuesto supera " + MAX_PRESUPUESTO_MATRIZ + ")");
        }
        mochila.mostrarResultado();
    }

    // ---------- Auxiliares ----------

    static void mostrarPublicaciones(Publicacion[] arr) {
        if (arr.length == 0) {
            System.out.println("(sin publicaciones)");
            return;
        }
        for (Publicacion p : arr) {
            System.out.println(p);
        }
    }

    private boolean usarArchivo() {
        System.out.println("\nOrigen de los datos:");
        System.out.println("  1. Datos de ejemplo");
        System.out.println("  2. Cargar desde archivo");
        int opcion;
        do {
            opcion = leerEntero("Opcion: ");
        } while (opcion != 1 && opcion != 2);
        return opcion == 2;
    }

    private String leerRuta(String rutaPorDefecto) {
        System.out.print("Ruta del archivo (Enter para '" + rutaPorDefecto + "'): ");
        String ruta = entrada.nextLine().trim();
        return ruta.isEmpty() ? rutaPorDefecto : ruta;
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = entrada.nextLine().trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un numero entero.");
            }
        }
    }

    private boolean confirmar(String mensaje) {
        System.out.print(mensaje);
        String texto = entrada.nextLine().trim();
        return texto.equalsIgnoreCase("s");
    }
}