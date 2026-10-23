import java.util.ArrayList;
import java.util.List;

public class MochilaIniciativas {

    private final List<Iniciativa> O;   // vector de iniciativas (los "objetos")
    private final int P;                // presupuesto (la "capacidad de la mochila")
    private final int[] peso;           // peso[i]  = costo de la iniciativa i
    private final int[] valor;          // valor[i] = impacto de la iniciativa i
    private int[][] M;                  // matriz de soluciones parciales

    // Resultado
    private int v;                                  // impacto maximo
    private List<Iniciativa> seleccionadas;
    private int costoTotal;

    public MochilaIniciativas(List<Iniciativa> iniciativas, int presupuesto) {
        if (presupuesto < 0) {
            throw new IllegalArgumentException("El presupuesto no puede ser negativo");
        }
        this.O = iniciativas;
        this.P = presupuesto;

        this.peso = new int[O.size()];
        this.valor = new int[O.size()];
        for (int i = 0; i < O.size(); i++) {
            peso[i] = O.get(i).getCosto();
            valor[i] = O.get(i).getImpacto();
        }
    }

    public void resolver() {
        v = mochila();
        reconstruir();
    }

    private int mochila() {
        // Caso borde: sin iniciativas no hay nada que elegir
        if (O.size() == 0) {
            M = new int[0][P + 1];
            return 0;
        }

        // M <- inicializarMatriz(longitud(O), P + 1)
        M = new int[O.size()][P + 1];

        // para i = 0 hasta longitud(O) - 1
        for (int i = 0; i <= O.size() - 1; i++) {
            // para j = 0 hasta P
            for (int j = 0; j <= P; j++) {
                // si i = 0 Y peso[i] > j
                if (i == 0 && peso[i] > j) {
                    M[i][j] = 0;
                } else {
                    // si i = 0
                    if (i == 0) {
                        M[i][j] = valor[i];
                    } else {
                        // si peso[i] > j
                        if (peso[i] > j) {
                            M[i][j] = M[i - 1][j];
                        } else {
                            M[i][j] = Math.max(M[i - 1][j], M[i - 1][j - peso[i]] + valor[i]);
                        } // fin si
                    } // fin si
                } // fin si
            } // fin para
        } // fin para

        // devolver M[longitud(O) - 1, P]
        return M[O.size() - 1][P];
    }


    private void reconstruir() {
        seleccionadas = new ArrayList<>();
        costoTotal = 0;
        int j = P;

        for (int i = O.size() - 1; i >= 0; i--) {
            boolean elegida;
            if (i == 0) {
                elegida = M[i][j] != 0;
            } else {
                elegida = M[i][j] != M[i - 1][j];
            }

            if (elegida) {
                seleccionadas.add(0, O.get(i)); // al principio, para mantener el orden original
                costoTotal += peso[i];
                j = j - peso[i];
            }
        }
    }

    // ---------- Resultados ----------

    public int getImpactoTotal() {
        return v;
    }

    public List<Iniciativa> getSeleccionadas() {
        return seleccionadas;
    }

    public int getCostoTotal() {
        return costoTotal;
    }

    public int getPresupuestoRestante() {
        return P - costoTotal;
    }

    public void mostrarResultado() {
        System.out.println("=== Seleccion de iniciativas (presupuesto: " + P + ") ===");
        if (seleccionadas.isEmpty()) {
            System.out.println("Ninguna iniciativa entra en el presupuesto.");
        } else {
            System.out.println("Iniciativas seleccionadas:");
            for (Iniciativa ini : seleccionadas) {
                System.out.println("  - " + ini);
            }
        }
        System.out.println("Costo total:          " + costoTotal);
        System.out.println("Impacto obtenido:     " + v);
        System.out.println("Presupuesto restante: " + getPresupuestoRestante());
    }

    /**
     * Imprime la matriz
     */
    public void mostrarMatriz() {
        System.out.print(String.format("%-12s%5s%5s |", "o", "Vo", "Po"));
        for (int j = 0; j <= P; j++) {
            System.out.print(String.format("%5d", j));
        }
        System.out.println();
        for (int i = 0; i < O.size(); i++) {
            System.out.print(String.format("%-12s%5d%5d |", O.get(i).getId(), valor[i], peso[i]));
            for (int j = 0; j <= P; j++) {
                System.out.print(String.format("%5d", M[i][j]));
            }
            System.out.println();
        }
    }
}
