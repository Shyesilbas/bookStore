package com.serhat.bookstore.Repository;

import com.serhat.bookstore.model.Book;
import com.serhat.bookstore.model.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book,Long> {
    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    boolean existsByTitle(String title);

    List<Book> findByGenre(Genre genre);

    List<Book> findByAuthor(String author);

    List<Book> findByTitle(String title);
}
