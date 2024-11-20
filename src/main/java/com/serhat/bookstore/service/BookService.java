package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    public String deleteBook(Long bookId){
        bookRepository.deleteById(bookId);
        return "Book Deleted : "+bookId;
    }
}
