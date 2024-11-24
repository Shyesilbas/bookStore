package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.BookRepository;
import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.exception.*;
import com.serhat.bookstore.model.Book;
import com.serhat.bookstore.model.BookStatus;
import com.serhat.bookstore.model.Genre;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    public List<BookResponse> listBooksByGenre(Genre genre ){

        List<Book> books = bookRepository.findByGenre(genre);
        return books.stream()
                .map(book -> new BookResponse(
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getGenre(),
                        book.getRate(),
                        book.getPrice()
                ))
                .toList();
    }

    public List<BookResponse> listBooksByRatingRange(double minRating , double maxRating){
        List<Book> books = bookRepository.findAll();

        if(minRating>maxRating){
            throw new IllegalRatingException("Check you rating ranges!");
        }
        return books.stream()
                .filter(book -> book.getRate()>= minRating && book.getRate()<=maxRating)
                .sorted(Comparator.comparingDouble(Book::getRate).reversed()) // Sort by rating
                .map(book -> new BookResponse(
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getGenre(),
                        book.getRate(),
                        book.getPrice()
                ))
                .toList();
    }

    public List<BookResponse> listBooksOfAuthor(String author){
        List<Book> books = bookRepository.findByAuthor(author);
        if(books.isEmpty()){
            throw new BookNotFoundForAuthorException("No such book found for author : "+author);
        }
        return books.stream()
                .map(book -> new BookResponse(
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getGenre(),
                        book.getRate(),
                        book.getPrice()
                ))
                .toList();
    }

    public List<BookResponse> findBookByTitle(String title){
        List<Book> books = bookRepository.findByTitle(title);
        if(books.isEmpty()){
            throw new BookNotFoundByTitleException("No Such book found for title : "+title);
        }
        return books.stream()
                .map(book -> new BookResponse(
                        book.getTitle(),
                        book.getAuthor(),
                        book.getIsbn(),
                        book.getGenre(),
                        book.getRate(),
                        book.getPrice()
                ))
                .toList();
    }

    @Transactional
    public UpdateStockResponse updateBookStock(UpdateStockRequest request){
        Optional<Book> book = bookRepository.findByIsbn(request.isbn());
        if(book.isEmpty()){
            throw new BookNotFoundException("Book not found by isbn : "+request.isbn());
        }
        if(request.newQuantity()<0){
            throw new IllegalStockUpdateException("quantity cannot be negative!");
        }
        int updatedQuantity = book.get().getQuantity()+ request.newQuantity();
        book.get().setQuantity(updatedQuantity);
        bookRepository.save(book.get());
        String adminUsername = getAdmin();
        log.info("Admin '{}' Updated  the book stock with ISBN '{}'. Book title: '{}'", adminUsername, request.isbn(), book.get().getTitle());
        log.info("New Stock : "+request.newQuantity());

        return new UpdateStockResponse(
                "Stock Updated Successfully",
                request.isbn(),
                request.newQuantity()
        );

    }


}
