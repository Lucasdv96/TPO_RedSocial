import java.util.Comparator;
/**
 * Ordenamiento por mezcla (Divide y Conquista), implementado desde cero.
 * No usa Arrays.sort ni Collections.sort.
 */

public class MergeSort {
    public static <T> void ordenar(T[] array, Comparator<T> comp){
        if(array == null || array.length < 2){
            return;
        }
        T[] aux = array.clone();
        ordenar(array, aux, 0, array.length -1, comp);
    }

    // DIVIDIR: partimos el rango en dos mitades y ordenamos cada una recursivamente.
    private static <T> void ordenar(T[] array,T[] aux, int izq, int der,Comparator<T> comp ){
        if(izq >= der){
            return; // caso base: 0 o 1 elemento ya ordenado
        }
        int medio =izq + (der - izq)/2;
        ordenar(array, aux, izq, medio, comp);
        ordenar(array, aux, medio + 1, der, comp);
        mezclar(array, aux, izq, medio, der, comp); //COMBINAR
    }

    // COMBINAR: mezcla dos mitades ya ordenadas en una sola.
    private static <T> void mezclar(T[] array, T[] aux, int izq, int medio ,int der, Comparator<T> comp){
        for (int k = izq; k <= der; k++) {
            aux[k] = array[k];
        }
        int i = izq;  //RECORRE LA MITAD IZQUIERDA
        int j = medio + 1;  //RECORRE LA MITAD DERECHA

        for(int k = izq; k <= der; k++){
            if( i > medio){
                array[k] = aux[j++];
            }else if (j > der){
                array[k] = aux[i++];
            }else if (comp.compare(aux[j], aux[i]) < 0){
                array[k] = aux[j++];
            } else{
                array[k] = aux[i++]; //en empate gana la izquierda (ordenamiento estable)
            }
        }
    }

}
