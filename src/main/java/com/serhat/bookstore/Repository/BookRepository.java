package com.serhat.bookstore.Repository;

import com.serhat.bookstore.model.Book;
import com.serhat.bookstore.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    boolean existsByTitle(String title);

    List<Book> findByGenre(Genre genre);

    List<Book> findByAuthor(String author);

    Optional<Book> findByTitle(String title);

    @Query("SELECT b FROM Book b WHERE b.releaseDate BETWEEN :startDate AND :endDate")
    List<Book> findByReleaseDateYear(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    Optional<Book> findBookByTitle(String title);
}
