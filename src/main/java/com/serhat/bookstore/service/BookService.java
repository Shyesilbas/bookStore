package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.BookRepository;
import com.serhat.bookstore.dto.AddBookRequest;
import com.serhat.bookstore.dto.AddBookResponse;
import com.serhat.bookstore.dto.DeleteBookResponse;
import com.serhat.bookstore.exception.BookNotFoundException;
import com.serhat.bookstore.exception.BookWithIsbnExistsException;
import com.serhat.bookstore.exception.BookWithTitleExistsException;
import com.serhat.bookstore.model.Book;
import com.serhat.bookstore.model.BookStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;

    private String getAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication instanceof JwtAuthenticationToken jwtAuthenticationToken){
            return jwtAuthenticationToken.getName();
        }
        return null;
    }

    @Transactional
    public DeleteBookResponse deleteBook (String isbn){
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(()-> new BookNotFoundException("Book Not Found! Isbn : "+isbn));
        bookRepository.delete(book);
        String adminUsername = getAdmin();
        log.info("Admin '{}' deleted the book with ISBN '{}'. Book title: '{}'", adminUsername, isbn, book.getTitle());
        return new DeleteBookResponse(
                "Book Deleted Successfully",
                isbn,
                book.getTitle()
        );
    }

    @Transactional
    public AddBookResponse addBook(AddBookRequest request){

        if(bookRepository.existsByIsbn(request.isbn())){
            throw new BookWithIsbnExistsException("Already a book exist with Isbn "+request.isbn());
        }
        if(bookRepository.existsByTitle(request.isbn())){
            throw new BookWithTitleExistsException("Already a book exist with Title "+request.title());
        }

        Book book = Book.builder()
                .isbn(request.isbn())
                .title(request.title())
                .author(request.author())
                .genre(request.genre())
                .releaseDate(request.releaseDate())
                .rate(request.rate())
                .bookStatus(BookStatus.AVAILABLE)
                .quantity(request.quantity())
                .price(request.price())
                .build();

        bookRepository.save(book);
        String adminUsername = getAdmin();
        log.info("Admin '{}' Added the book with ISBN '{}'. Book title: '{}'", adminUsername, request.isbn(), book.getTitle());
        return new AddBookResponse(
                "Book Added Successfully",
                request.title(),
                request.author(),
                request.isbn(),
                request.genre(),
                request.rate(),
                request.price()
        );

    }

}
