package com.serhat.bookstore.Controller;

import com.serhat.bookstore.service.BookService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/book")
public class BookController {
    private final BookService bookService;

    @DeleteMapping("/delete/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable Long bookId){
        return ResponseEntity.ok(bookService.deleteBook(bookId));
    }
}
