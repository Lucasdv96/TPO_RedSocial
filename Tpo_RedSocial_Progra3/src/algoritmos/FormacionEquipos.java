package algoritmos;

import modelo.Estudiante;

import java.util.ArrayList;
import java.util.List;


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


    public void resolver() {
        equiposValidos.clear();
        estadosExplorados = 0;
        if (v.isEmpty()) {
            return;
        }
        int[] actSolucion = new int[v.size()];
        formarEquipos(actSolucion, 0, 0);
    }


    private void formarEquipos(int[] actSolucion, int actCantidad, int etapa) {
        // para i = 0 hasta 1
        for (int i = 0; i <= 1; i++) {
            estadosExplorados++;

            actSolucion[etapa] = i;
            int nuevaCantidad = actCantidad + i;

            if (factible(actSolucion, nuevaCantidad, etapa)) {
                if (etapa == v.size() - 1) {
                    if (esSolucion(actSolucion, nuevaCantidad)) {
                        guardarEquipo(actSolucion);
                    }
                } else {
                    formarEquipos(actSolucion, nuevaCantidad, etapa + 1);
                }
            }
        }
        actSolucion[etapa] = 0;
    }

    private boolean factible(int[] actSolucion, int cantidad, int etapa) {

        if (actSolucion[etapa] == 0 && obligatorio[etapa]) {
            return false;
        }
        if (cantidad > tamanio) {
            return false;
        }
        if (actSolucion[etapa] == 1) {
            for (int j = 0; j < etapa; j++) {
                if (actSolucion[j] == 1 && incompatible[etapa][j]) {
                    return false;
                }
            }
        }
        int quedan = v.size() - 1 - etapa;
        if (cantidad + quedan < tamanio) {
            return false;
        }
        return true;
    }

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
