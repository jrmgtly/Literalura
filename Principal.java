package com.konectape.literalura.principal;

import com.konectape.literalura.model.*;
import com.konectape.literalura.repository.AutorRepository;
import com.konectape.literalura.repository.LibroRepository;
import com.konectape.literalura.services.ConsumoApi;
import com.konectape.literalura.services.ConvierteDatos;

import java.util.DoubleSummaryStatistics;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Integer opcion = -1;
    private Boolean isRunApp = true;
    private final Scanner sc = new Scanner(System.in);
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConvierteDatos conversor = new ConvierteDatos();
    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;
    private final String URL_BASE = "https://gutendex.com/books/?search=";

    public Principal(AutorRepository autorRepository, LibroRepository libroRepository) {
        this.autorRepository = autorRepository;
        this.libroRepository = libroRepository;
    }

    public void muestraElMenu() {
        String menu = """
                --------------------------------------------
                \nElija la opción a través de un número:
                1 - Búsqueda de libro por titulo.
                2 - Listar todos los libros registrados.
                3 - Listar todos los autores registrados.
                4 - Listar autores vivos en un determinado año.
                5 - Listar libros por idioma.
                6 - Obtener estadísticas.
                0 - Salir.
                --------------------------------------------
                """;

        while (isRunApp) {
            try {
                System.out.println(menu);
                opcion = sc.nextInt();
                sc.nextLine();
                switch (opcion) {
                    case 1:
                        agregarLibro();
                        break;
                    case 2:
                        listarLibrosRegistrados();
                        break;
                    case 3:
                        listarAutoresRegistrados();
                        break;
                    case 4:
                        listarAutoresVivos();
                        break;
                    case 5:
                        listarLibrosPorIdioma();
                        break;
                    case 6:
                        obtenerEstadisticas();
                        break;
                    case 0:
                        isRunApp = false;
                        System.out.println("Saliendo de la aplicación...");
                        break;
                    default:
                        System.out.println("Opcion inválida.");
                }
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.println("Ingrese un número de opción válida: " + e.getMessage());
            }
        }
    }

    private DatosResultado busquedaLibroPorTitulo() {
        System.out.println("Ingresa el nombre del libro que desea buscar:");
        String nombreLibro = sc.nextLine();
        String json = consumoApi.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "+"));
        return conversor.obtenerDatos(json, DatosResultado.class);
    }

    private void agregarLibro() {
        DatosResultado datosResultado = busquedaLibroPorTitulo();
        if (!datosResultado.datosDeLibro().isEmpty()) {
            DatosLibro datosLibro = datosResultado.datosDeLibro().getFirst();
            DatosAutor datosAutor = datosLibro.datosDeAutor().getFirst();
            var tituloDeLibro = libroRepository.findLibroByTitulo(datosLibro.titulo());
            if (tituloDeLibro != null) {
                System.out.println("No se puede registrar el mismo libro más de una vez.");
            } else {
                var autorDeLibro = autorRepository.findAutorByNombreIgnoreCase(datosAutor.nombreDeAutor());
                Libro libro;
                if (autorDeLibro != null) {
                    libro = new Libro(datosLibro, autorDeLibro);
                } else {
                    Autor autor = new Autor(datosAutor);
                    autorRepository.save(autor);
                    libro = new Libro(datosLibro, autor);
                }
                libroRepository.save(libro);
                System.out.println("--------- LIBRO GUARDADO ---------");
                System.out.println(libro);
            }
        } else {
            System.out.println("El libro no existe, intentelo de nuevo.");
        }
    }

    private void listarLibrosRegistrados() {
        List<Libro> libros = libroRepository.findAll();
        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            libros.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            autores.forEach(System.out::println);
        }
    }

    private void listarAutoresVivos() {
        System.out.println("Ingrese el año vivo de autor(es) que desea buscar.");
        var fechaAutor = sc.nextInt();
        sc.nextLine();
        if (fechaAutor < 0) {
            System.out.println("Has ingresado un año negativo, intenta de nuevo.");
        } else {
            List<Autor> fechaAutores = autorRepository.findAutorByFechaDeNacimientoLessThanEqualAndFechaDeFallecimientoGreaterThanEqual(fechaAutor, fechaAutor);
            if (fechaAutores.isEmpty()) {
                System.out.println("No hay autores registrados en ese año.");
            } else {
                fechaAutores.forEach(System.out::println);
            }
        }
    }

    private void listarLibrosPorIdioma() {
        String menu = """
                Ingrese el idioma para buscar los libros:
                es - español
                en - inglés
                fr - francés
                pt - portugués
                """;
        System.out.println(menu);
        String idioma = sc.nextLine();
        if (!idioma.equals("es") && !idioma.equals("en") && !idioma.equals("fr") && !idioma.equals("pt")) {
            System.out.println("Has ingresado un idioma incorrecto, intentalo de nuevo.");
        } else {
            List<Libro> librosPorIdioma = libroRepository.findLibrosByIdiomasContaining(idioma);
            if (librosPorIdioma.isEmpty()) {
                System.out.println("No hay libros registrados en ese idioma.");
            } else {
                int cantidadLibros = librosPorIdioma.size();
                System.out.println("Total libros registrados en %s: ".formatted(Idioma.fromString(idioma)) + cantidadLibros);
                librosPorIdioma.forEach(System.out::println);
            }
        }
    }

    private void obtenerEstadisticas() {
        System.out.println("¿De donde quiere obtener las estadísticas?");
        String menu = """
                1 - Gutendex
                2 - Base de datos
                """;
        System.out.println(menu);
        var opcion = sc.nextInt();
        sc.nextLine();
        if (opcion == 1) {
            System.out.println("----- ESTADÍSTICAS DE DESCARGAS EN GUTENDEX -----");
            String json = consumoApi.obtenerDatos(URL_BASE);
            DatosResultado datosResultado = conversor.obtenerDatos(json, DatosResultado.class);
            DoubleSummaryStatistics estadisticas = datosResultado.datosDeLibro()
                    .stream()
                    .collect(Collectors.summarizingDouble(DatosLibro::cantidadDescargas));
            System.out.println("Libro con más descargas: " + estadisticas.getMax());
            System.out.println("Libro con menos descargas: " + estadisticas.getMin());
            System.out.println("Promedio de descargas: " + estadisticas.getAverage());
        } else if (opcion == 2) {
            System.out.println("----- ESTADÍSTICAS DE DESCARGAS EN BASE DE DATOS -----");
            List<Libro> libros = libroRepository.findAll();
            if (libros.isEmpty()) {
                System.out.println("No hay libros registrados en la base de datos.");
            } else {
                DoubleSummaryStatistics estadisticas = libros
                        .stream()
                        .collect(Collectors.summarizingDouble(Libro::getCantidadDescargas));
                System.out.println("Libro con más descargas: " + estadisticas.getMax());
                System.out.println("Libro con menos descargas: " + estadisticas.getMin());
                System.out.println("Promedio de descargas: " + estadisticas.getAverage());
            }
        } else {
            System.out.println("Opción no válida, intentelo de nuevo.");
        }
    }
}
