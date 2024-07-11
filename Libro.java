package com.konectape.literalura.model;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String titulo;
    private String nombreAutor;
    private String idiomas;
    private Double cantidadDescargas;
    @ManyToOne
    @JoinColumn(name = "autor_id", nullable = false)
    private Autor autor;

    public Libro() {

    }

    public Libro(DatosLibro datosLibro, Autor autor) {
        this.titulo = datosLibro.titulo();
        this.nombreAutor = autor.getNombre();
        this.idiomas = datosLibro.idiomas().getFirst();
        this.cantidadDescargas = datosLibro.cantidadDescargas();
        this.autor = autor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getNombreAutor() {
        return nombreAutor;
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

    public String getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(String idiomas) {
        this.idiomas = idiomas;
    }

    public Double getCantidadDescargas() {
        return cantidadDescargas;
    }

    public void setCantidadDescargas(Double cantidadDescargas) {
        this.cantidadDescargas = cantidadDescargas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor datosDeAutor) {
        this.autor = datosDeAutor;
    }

    @Override
    public String toString() {
        return """
                ---------------------------------------------------
                Titulo: %s
                Autor: %s
                Idioma: %s
                Número de descargas: %.2f
                ---------------------------------------------------
                """.formatted(titulo, nombreAutor, idiomas, cantidadDescargas);
    }
}
