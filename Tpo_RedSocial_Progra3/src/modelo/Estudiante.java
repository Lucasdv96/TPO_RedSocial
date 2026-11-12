package modelo;

import java.util.ArrayList;
import java.util.List;

public class Estudiante {

    private final String nombre;
    private final List<String> habilidades;

    public Estudiante(String nombre, List<String> habilidades) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El estudiante necesita un nombre");
        }
        if (habilidades == null) {
            throw new IllegalArgumentException("El estudiante " + nombre + " necesita una lista de habilidades");
        }
        this.nombre = nombre;
        this.habilidades = new ArrayList<>(habilidades);
    }

    public String getNombre() {
        return nombre;
    }

    public List<String> getHabilidades() {
        return habilidades;
    }

    public boolean tieneHabilidad(String habilidad) {
        return habilidades.contains(habilidad);
    }

    @Override
    public String toString() {
        return nombre + " " + habilidades;
    }
}
