package com.serhat.bookstore.Controller;

import com.serhat.bookstore.dto.AddBookRequest;
import com.serhat.bookstore.dto.AddBookResponse;
import com.serhat.bookstore.dto.DeleteBookResponse;
import com.serhat.bookstore.service.BookService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
