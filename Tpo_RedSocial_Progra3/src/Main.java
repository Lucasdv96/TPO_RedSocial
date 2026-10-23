import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        Muro muro = new Muro();
        LocalDateTime base = LocalDateTime.of(2026, 10, 20, 12, 0);

        muro.agregar(new Publicacion(1, "Ana",   base.plusHours(1), 10, 2));
        muro.agregar(new Publicacion(2, "Luis",  base.plusHours(5),  3, 1));
        muro.agregar(new Publicacion(3, "Sofi",  base.plusHours(3), 10, 2)); // misma relevancia que id 1
        muro.agregar(new Publicacion(4, "Mati",  base.plusHours(3),  1, 0)); // misma fecha que id 3
        muro.agregar(new Publicacion(5, "Caro",  base.plusHours(2),  7, 5));

        System.out.println("=== Por fecha (más reciente primero) ===");
        mostrar(muro.constultarPorFecha());

        System.out.println("\n=== Por relevancia (más relevante primero) ===");
        mostrar(muro.consultarPorRelevancia());

        // Casos borde
        System.out.println("\n=== Muro vacío ===");
        mostrar(new Muro().constultarPorFecha());

        Muro unico = new Muro();
        unico.agregar(new Publicacion(9, "Solo", base, 0, 0));
        System.out.println("\n=== Una sola publicación ===");
        mostrar(unico.consultarPorRelevancia());
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
