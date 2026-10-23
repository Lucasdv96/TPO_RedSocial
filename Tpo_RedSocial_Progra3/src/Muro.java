import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Muro {

    // MAS RECIENTE PRIMERO. SI HAY EMPATE DE FECHA, ID MENOR PRIMERO.
    private static final Comparator<Publicacion> POR_FECHA= (a,b) -> {
        int c = b.getFechaHora().compareTo(a.getFechaHora());
        if (c != 0) return c;
        return Integer.compare(a.getId(),b.getId());
    };

    // MAS RELEVANTE PRIMERP. EMPATE: MAS RECIENTES. NUEVO EMPATE: ID MENOR.
    private static final Comparator<Publicacion> POR_RELEVANCIA = (a,b) -> {
        int c = Integer.compare(b.getRelevancia(),a.getRelevancia());
        if (c != 0) return c;
        c = b.getFechaHora().compareTo(a.getFechaHora());
        if(c != 0) return c;
        return Integer.compare(a.getId(),b.getId());
    };

    private final List<Publicacion> publicaciones = new ArrayList<>();

    public void agregar(Publicacion p) {
        publicaciones.add(p);
    }

    public Publicacion [] constultarPorFecha(){
        return ordenadas(POR_FECHA);
    }

    public Publicacion[] consultarPorRelevancia() {
        return ordenadas(POR_RELEVANCIA);
    }

    private Publicacion[] ordenadas(Comparator<Publicacion> comp){
        Publicacion[] copia = publicaciones.toArray(new Publicacion[0]);
        MergeSort.ordenar(copia, comp);
        return copia;
    }
}
