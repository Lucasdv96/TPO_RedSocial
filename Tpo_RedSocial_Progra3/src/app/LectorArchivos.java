package app;

import estructuras.Grafo;
import modelo.Iniciativa;
import modelo.Publicacion;
import servicios.Muro;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Lee los datos de entrada desde archivos de texto.
 * Reglas comunes: un registro por linea, campos separados por ';',
 * se ignoran las lineas vacias y las que empiezan con '#'.
 */
public class LectorArchivos {

    /** Resultado de leer un archivo de iniciativas: la lista y el presupuesto. */
    public static class DatosMochila {
        private final List<Iniciativa> iniciativas;
        private final int presupuesto;

        public DatosMochila(List<Iniciativa> iniciativas, int presupuesto) {
            this.iniciativas = iniciativas;
            this.presupuesto = presupuesto;
        }

        public List<Iniciativa> getIniciativas() { return iniciativas; }
        public int getPresupuesto() { return presupuesto; }
    }

    private static class Linea {
        final int numero;
        final String[] campos;

        Linea(int numero, String[] campos) {
            this.numero = numero;
            this.campos = campos;
        }
    }

    // ---------- Punto 1: publicaciones ----------
    // Formato: id;autor;fechaHora;reacciones;comentarios
    // Ejemplo: 1;Ana;2026-10-20T13:00;10;2

    public static Muro leerMuro(String ruta) throws IOException {
        Muro muro = new Muro();
        Set<Integer> ids = new HashSet<>();
        for (Linea l : leerLineas(ruta)) {
            try {
                exigirCampos(l, 5, "id;autor;fechaHora;reacciones;comentarios");
                int id = entero(l, 0, "id");
                String autor = l.campos[1];
                LocalDateTime fecha = fecha(l, 2);
                int reacciones = entero(l, 3, "reacciones");
                int comentarios = entero(l, 4, "comentarios");
                if (autor.isEmpty()) {
                    throw new IllegalArgumentException("el autor no puede estar vacio");
                }
                if (reacciones < 0 || comentarios < 0) {
                    throw new IllegalArgumentException("reacciones y comentarios no pueden ser negativos");
                }
                if (!ids.add(id)) {
                    throw new IllegalArgumentException("el id " + id + " esta repetido");
                }
                muro.agregar(new Publicacion(id, autor, fecha, reacciones, comentarios));
            } catch (IllegalArgumentException e) {
                throw errorEnLinea(l, e);
            }
        }
        return muro;
    }

    // ---------- Punto 2: red de usuarios ----------
    // Formato: origen;destino;minutos   (vinculo dirigido)
    //          usuario                  (usuario sin vinculos)

    public static Grafo leerRed(String ruta) throws IOException {
        Grafo red = new Grafo();
        for (Linea l : leerLineas(ruta)) {
            try {
                if (l.campos.length == 1) {
                    red.agregarVertice(entero(l, 0, "usuario"));
                } else {
                    exigirCampos(l, 3, "origen;destino;minutos");
                    red.agregarArista(entero(l, 0, "origen"), entero(l, 1, "destino"), entero(l, 2, "minutos"));
                }
            } catch (IllegalArgumentException e) {
                throw errorEnLinea(l, e);
            }
        }
        return red;
    }

    // ---------- Punto 3: iniciativas ----------
    // Primera linea: presupuesto;N
    // Resto:         id;costo;impacto

    public static DatosMochila leerIniciativas(String ruta) throws IOException {
        List<Linea> lineas = leerLineas(ruta);
        if (lineas.isEmpty() || !lineas.get(0).campos[0].equalsIgnoreCase("presupuesto")) {
            throw new IllegalArgumentException("la primera linea del archivo debe ser: presupuesto;N");
        }

        int presupuesto;
        Linea primera = lineas.get(0);
        try {
            exigirCampos(primera, 2, "presupuesto;N");
            presupuesto = entero(primera, 1, "presupuesto");
            if (presupuesto < 0) {
                throw new IllegalArgumentException("el presupuesto no puede ser negativo");
            }
        } catch (IllegalArgumentException e) {
            throw errorEnLinea(primera, e);
        }

        List<Iniciativa> iniciativas = new ArrayList<>();
        for (Linea l : lineas.subList(1, lineas.size())) {
            try {
                exigirCampos(l, 3, "id;costo;impacto");
                iniciativas.add(new Iniciativa(l.campos[0], entero(l, 1, "costo"), entero(l, 2, "impacto")));
            } catch (IllegalArgumentException e) {
                throw errorEnLinea(l, e);
            }
        }
        return new DatosMochila(iniciativas, presupuesto);
    }

    // ---------- Auxiliares ----------

    private static List<Linea> leerLineas(String ruta) throws IOException {
        Path path = Paths.get(ruta);
        if (!Files.isRegularFile(path)) {
            throw new IOException("No se encontro el archivo '" + ruta + "' (carpeta de trabajo: "
                    + System.getProperty("user.dir") + ")");
        }
        List<String> crudas = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<Linea> resultado = new ArrayList<>();
        for (int i = 0; i < crudas.size(); i++) {
            String texto = crudas.get(i);
            if (i == 0 && texto.startsWith("\uFEFF")) {
                texto = texto.substring(1); // marca BOM de algunos editores
            }
            texto = texto.trim();
            if (texto.isEmpty() || texto.startsWith("#")) {
                continue;
            }
            String[] campos = texto.split(";", -1);
            for (int k = 0; k < campos.length; k++) {
                campos[k] = campos[k].trim();
            }
            resultado.add(new Linea(i + 1, campos));
        }
        return resultado;
    }

    private static void exigirCampos(Linea l, int cantidad, String formato) {
        if (l.campos.length != cantidad) {
            throw new IllegalArgumentException("se esperaban " + cantidad + " campos (" + formato
                    + ") y hay " + l.campos.length);
        }
    }

    private static int entero(Linea l, int posicion, String nombre) {
        try {
            return Integer.parseInt(l.campos[posicion]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + l.campos[posicion] + "' no es un numero valido para " + nombre);
        }
    }

    private static LocalDateTime fecha(Linea l, int posicion) {
        try {
            return LocalDateTime.parse(l.campos[posicion]);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("'" + l.campos[posicion]
                    + "' no es una fecha valida (formato aaaa-MM-ddTHH:mm, por ejemplo 2026-10-20T13:00)");
        }
    }

    private static IllegalArgumentException errorEnLinea(Linea l, Exception e) {
        return new IllegalArgumentException("linea " + l.numero + ": " + e.getMessage());
    }
}