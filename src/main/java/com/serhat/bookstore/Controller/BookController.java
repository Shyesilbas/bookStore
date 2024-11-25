package com.serhat.bookstore.Controller;

import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.model.Genre;
import com.serhat.bookstore.service.BookService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;

    @DeleteMapping("/delete/{isbn}")
    public ResponseEntity<DeleteBookResponse> deleteBook (@PathVariable String isbn){
        return ResponseEntity.ok(bookService.deleteBook(isbn));
    }
    @PostMapping("/addBook")
    public ResponseEntity<AddBookResponse> addBook (@RequestBody AddBookRequest request){
        return ResponseEntity.ok(bookService.addBook(request));
    }
    @GetMapping("/books/genre/{genre}")
    public ResponseEntity<List<BookResponse>> listBooksByGenre(@PathVariable Genre genre){
        return ResponseEntity.ok(bookService.listBooksByGenre(genre));
    }

    @GetMapping("/byRateRange/{minRating}/{maxRating}")
    public ResponseEntity<List<BookResponse>> listBooksByRatingRange(@PathVariable double minRating , @PathVariable double maxRating){
        List<BookResponse> bookResponses = bookService.listBooksByRatingRange(minRating, maxRating);

        if (bookResponses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
        return ResponseEntity.ok(bookResponses);
    }

    @GetMapping("/byAuthor")
    public ResponseEntity<List<BookResponse>> listBooksOfAuthor(@RequestParam String author){
        return ResponseEntity.ok(bookService.listBooksOfAuthor(author));
    }

    @GetMapping("/byTitle")
    public ResponseEntity<List<BookResponse>> listBookByTitle(@RequestParam String title){
        return ResponseEntity.ok(bookService.findBookByTitle(title));
    }

    @PutMapping("/updateStock")
    public ResponseEntity<UpdateStockResponse> updateBookStock(@RequestBody UpdateStockRequest request){
        return ResponseEntity.ok(bookService.updateBookStock(request));
    }
    @PutMapping("/updatePrice")
    public ResponseEntity<UpdateBookPriceResponse> updatePrice(@RequestBody UpdatePriceRequest request){
        return ResponseEntity.ok(bookService.updatePrice(request));
    }
    @GetMapping("/details")
    public ResponseEntity<BookResponse> getBookDetails(@RequestParam String isbn){
        return ResponseEntity.ok(bookService.getBookDetails(isbn));
    }

    @GetMapping("/byYear")
    public ResponseEntity<List<BookResponse>> getBooksByYear(@RequestParam LocalDate startDate , @RequestParam LocalDate endDate){
        return ResponseEntity.ok(bookService.listBooksByYear(startDate,endDate));
    }
    @GetMapping("/by-price")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<BookResponse>> listBooksByPrice(
            @RequestParam(defaultValue = "true") boolean ascending) {
        List<BookResponse> books = bookService.listBooksByPrice(ascending);
        return ResponseEntity.ok(books);
    }
    @GetMapping("/by-rate")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<BookResponse>> listBooksByRate(
            @RequestParam(defaultValue = "true") boolean ascending) {
        List<BookResponse> books = bookService.listBooksByRate(ascending);
        return ResponseEntity.ok(books);
    }



}
