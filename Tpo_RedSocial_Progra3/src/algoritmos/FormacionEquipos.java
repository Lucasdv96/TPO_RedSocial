package algoritmos;

import modelo.Estudiante;

import java.util.ArrayList;
import java.util.List;

/**
 * B1 - Formacion de equipos con restricciones.
 *
 * ESTRATEGIA: Backtracking (igual que "Suma de Subconjuntos", clase 16/10).
 *
 * Para cada estudiante (etapa) se decide:  0 = no entra al equipo,  1 = entra.
 * La solucion es un vector ActSolucion de 0 y 1, uno por estudiante.
 *
 * Como cada estudiante se decide una sola vez y siempre en el mismo orden,
 * cada equipo se genera una unica vez: nunca aparece {Ana, Beto} y {Beto, Ana}.
 *
 * Restricciones:
 *   - Explicitas: ActSolucion[etapa] pertenece a {0, 1}.
 *   - Implicitas: el equipo tiene exactamente "tamanio" integrantes, cubre todas
 *     las habilidades requeridas, incluye a los obligatorios y no junta
 *     a dos estudiantes incompatibles.
 *
 * Podas (funcion factible): se corta la rama apenas no puede llegar a una solucion.
 *
 * Complejidad temporal: a = 2, b = 1, k = 0  ->  O(2^n)
 *   (n = cantidad de estudiantes; cada nodo ademas revisa incompatibilidades en O(n),
 *    por lo que el total es O(2^n * n). Las podas achican mucho el caso real.)
 */
public class FormacionEquipos {

    private final List<Estudiante> v;              // vector de estudiantes
    private final int tamanio;                     // tamanio del equipo
    private final List<String> habilidadesRequeridas;
    private final boolean[] obligatorio;           // obligatorio[i] = el estudiante i debe estar
    private final boolean[][] incompatible;        // incompatible[i][j] = i y j no pueden estar juntos

    // Resultado
    private final List<List<Estudiante>> equiposValidos = new ArrayList<>();
    private int estadosExplorados;

    public FormacionEquipos(List<Estudiante> estudiantes, int tamanio, List<String> habilidadesRequeridas,
                            List<String> obligatorios, List<String[]> paresIncompatibles) {
        if (tamanio <= 0) {
            throw new IllegalArgumentException("El tamanio del equipo debe ser positivo");
        }
        this.v = estudiantes;
        this.tamanio = tamanio;
        this.habilidadesRequeridas = habilidadesRequeridas;

        int n = estudiantes.size();
        this.obligatorio = new boolean[n];
        for (String nombre : obligatorios) {
            obligatorio[buscarIndice(nombre)] = true;
        }

        this.incompatible = new boolean[n][n];
        for (String[] par : paresIncompatibles) {
            int a = buscarIndice(par[0]);
            int b = buscarIndice(par[1]);
            incompatible[a][b] = true;
            incompatible[b][a] = true;
        }
    }

    /**
     * Busca todos los equipos validos.
     */
    public void resolver() {
        equiposValidos.clear();
        estadosExplorados = 0;
        if (v.isEmpty()) {
            return;
        }
        int[] actSolucion = new int[v.size()];
        formarEquipos(actSolucion, 0, 0);
    }

    /**
     * ALGORITMO formarEquipos (basado en el algoritmo sumaM de la clase)
     *
     * Entrada: ActSolucion: Vector<entero>, ActCantidad: entero, etapa: entero
     */
    private void formarEquipos(int[] actSolucion, int actCantidad, int etapa) {
        // para i = 0 hasta 1
        for (int i = 0; i <= 1; i++) {
            estadosExplorados++;

            // ActSolucion[etapa] <- i
            actSolucion[etapa] = i;
            // ActCantidad <- ActCantidad + i
            int nuevaCantidad = actCantidad + i;

            // si factible (si no, se poda la rama)
            if (factible(actSolucion, nuevaCantidad, etapa)) {
                // si etapa = tamanio(v) - 1   (ya se decidio por todos los estudiantes)
                if (etapa == v.size() - 1) {
                    // si es solucion -> mostrar / guardar
                    if (esSolucion(actSolucion, nuevaCantidad)) {
                        guardarEquipo(actSolucion);
                    } // fin si
                } else {
                    formarEquipos(actSolucion, nuevaCantidad, etapa + 1);
                } // fin si
            } // fin si
        } // fin para

        // eliminar(S, E): se deja la etapa sin decidir antes de volver atras
        actSolucion[etapa] = 0;
    }

    /**
     * Funcion FACTIBLE: devuelve falso (poda) si la solucion parcial ya no puede ser valida.
     */
    private boolean factible(int[] actSolucion, int cantidad, int etapa) {
        // Poda 1: un obligatorio no puede quedar afuera
        if (actSolucion[etapa] == 0 && obligatorio[etapa]) {
            return false;
        }
        // Poda 2: el equipo no puede pasarse del tamanio
        if (cantidad > tamanio) {
            return false;
        }
        // Poda 3: el que entra no puede ser incompatible con alguien que ya esta
        if (actSolucion[etapa] == 1) {
            for (int j = 0; j < etapa; j++) {
                if (actSolucion[j] == 1 && incompatible[etapa][j]) {
                    return false;
                }
            }
        }
        // Poda 4: tienen que quedar suficientes estudiantes para completar el equipo
        int quedan = v.size() - 1 - etapa;
        if (cantidad + quedan < tamanio) {
            return false;
        }
        return true;
    }

    /**
     * Funcion SOLUCION: el equipo tiene el tamanio pedido y cubre todas las habilidades.
     */
    private boolean esSolucion(int[] actSolucion, int cantidad) {
        if (cantidad != tamanio) {
            return false;
        }
        for (String habilidad : habilidadesRequeridas) {
            boolean cubierta = false;
            for (int i = 0; i < v.size() && !cubierta; i++) {
                if (actSolucion[i] == 1 && v.get(i).tieneHabilidad(habilidad)) {
                    cubierta = true;
                }
            }
            if (!cubierta) {
                return false;
            }
        }
        return true;
    }

    private void guardarEquipo(int[] actSolucion) {
        List<Estudiante> equipo = new ArrayList<>();
        for (int i = 0; i < v.size(); i++) {
            if (actSolucion[i] == 1) {
                equipo.add(v.get(i));
            }
        }
        equiposValidos.add(equipo);
    }

    private int buscarIndice(String nombre) {
        for (int i = 0; i < v.size(); i++) {
            if (v.get(i).getNombre().equals(nombre)) {
                return i;
            }
        }
        throw new IllegalArgumentException("No existe el estudiante: " + nombre);
    }

    // ---------- Resultados ----------

    public List<List<Estudiante>> getEquiposValidos() {
        return equiposValidos;
    }

    public int getEstadosExplorados() {
        return estadosExplorados;
    }

    /**
     * Muestra todos los equipos validos encontrados.
     */
    public void mostrarResultado() {
        System.out.println("=== Formacion de equipos (tamanio " + tamanio
                + ", habilidades requeridas " + habilidadesRequeridas + ") ===");
        if (equiposValidos.isEmpty()) {
            System.out.println("No hay ningun equipo valido.");
        } else {
            int numero = 1;
            for (List<Estudiante> equipo : equiposValidos) {
                System.out.print("Equipo " + numero + ": ");
                for (int i = 0; i < equipo.size(); i++) {
                    System.out.print((i > 0 ? ", " : "") + equipo.get(i).getNombre());
                }
                System.out.println();
                numero++;
            }
        }
        System.out.println("Equipos validos:     " + equiposValidos.size());
        System.out.println("Estados explorados:  " + estadosExplorados
                + " (sin podas serian " + (long) (Math.pow(2, v.size() + 1) - 2) + ")");
    }
}
