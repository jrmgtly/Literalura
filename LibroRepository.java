package com.konectape.literalura.repository;

import com.konectape.literalura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    Libro findLibroByTitulo(String nombre);
    List<Libro> findLibrosByIdiomasContaining(String idiomas);
}
