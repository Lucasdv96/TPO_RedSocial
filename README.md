# TPO Red Social Universitaria - Programación III

Red social universitaria en Java. Seis funcionalidades: las tres obligatorias y tres elegidas de los bloques A y B,
cubriendo Divide y Conquista, Greedy, Programación Dinámica y Backtracking.

| Funcionalidad | Estrategia | Complejidad | Menú | Autor |
|---|---|---|---|---|
| 1. Muro de publicaciones | Divide y Conquista (Merge Sort propio) | O(n log n) | 1 | Lucas Del Valle |
| 2. Cadena de difusión más rápida | Greedy (Dijkstra) | O(V²) | 2 | Brisa Nuñez |
| 3. Selección de iniciativas a financiar | Programación Dinámica (mochila 0/1) | O(n · P) | 3 | Nestor Guarachi |
| A3. Selección de actividades compatibles | Programación Dinámica (intervalos con peso) | O(n log n) | 7 | Brisa Nuñez |
| B1. Formación de equipos con restricciones | Backtracking | O(2ⁿ · n) | 6 | Nestor Guarachi |
| B3. Cadenas alternativas de difusión | Backtracking | O(V!) | 5 | Lucas Del Valle |

La opción 4 del menú ejecuta los casos de prueba predefinidos de todas las funcionalidades.

## Estructura

```
Tpo_RedSocial_Progra3/
├── datos/           archivos de entrada de ejemplo
└── src/
    ├── app/         Main, Menu, LectorArchivos, CasosDePrueba
    ├── modelo/      Publicacion, Iniciativa, ResultadoDifusion, Actividad,
    │                Estudiante, Cadena, ResultadoCadenas
    ├── estructuras/ Grafo
    ├── algoritmos/  MergeSort, CadenaDifusion, MochilaIniciativas,
    │                SeleccionActividades, FormacionEquipos, CadenasAlternativas
    └── servicios/   Muro
```

## Cómo ejecutar

Desde IntelliJ: ejecutar `app.Main`. La carpeta de trabajo tiene que ser `Tpo_RedSocial_Progra3`.

Desde terminal, parado en `Tpo_RedSocial_Progra3`:

```
javac -d out -sourcepath src src/app/Main.java
java -cp out app.Main
```

## Menú

| Opción | Funcionalidad | Datos |
|---|---|---|
| 1 | Muro de publicaciones | Ejemplo o `datos/publicaciones.txt` |
| 2 | Cadena de difusión más rápida | Ejemplo o `datos/red.txt`; se ingresan origen y destino |
| 3 | Selección de iniciativas | Ejemplo o `datos/iniciativas.txt` |
| 4 | Casos de prueba predefinidos | Todas las funcionalidades |
| 5 | Cadenas alternativas de difusión | Ejemplo o `datos/red.txt`; se ingresan origen, destino, tiempo máximo, límite de intermediarios y excluidos |
| 6 | Formación de equipos | Ejemplo o `datos/equipos.txt` |
| 7 | Selección de actividades compatibles | Ejemplo o `datos/actividades.txt` |

Al elegir "Cargar desde archivo", Enter usa la ruta por defecto de la tabla.

## Formato de los archivos de entrada

Un registro por línea, campos separados por `;`. Se ignoran las líneas vacías y las que empiezan con `#`.

- **Publicaciones:** `id;autor;fechaHora;reacciones;comentarios`, por ejemplo `1;Ana;2026-10-20T13:00;10;2`.
- **Red de usuarios:** `origen;destino;minutos` para cada vínculo dirigido. Un usuario sin vínculos se escribe solo con su número. La usan las opciones 2 y 5.
- **Iniciativas:** la primera línea es `presupuesto;N` y el resto `id;costo;impacto`.
- **Actividades:** `id;horaInicio;horaFin;interes`, con horas en formato `HH:mm`, por ejemplo `Taller;09:00;11:30;8`.
- - **Equipos:** cada línea empieza con su tipo: `tamanio;N` (obligatoria), `requeridas;hab1,hab2`, `estudiante;nombre;hab1,hab2` (una por estudiante), `obligatorios;nombre1,nombre2` e `incompatibles;nombre1-nombre2`.

Si un archivo tiene un error, el programa indica la línea y el motivo.
