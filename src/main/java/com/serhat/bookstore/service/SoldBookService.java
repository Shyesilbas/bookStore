package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.BookRepository;
import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.Repository.SoldBookRepository;
import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.exception.BookNotFoundException;
import com.serhat.bookstore.exception.BookOutOfStocksException;
import com.serhat.bookstore.exception.CustomerNotFoundException;
import com.serhat.bookstore.exception.NoBooksSoldException;
import com.serhat.bookstore.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SoldBookService {
    private final SoldBookRepository soldBookRepository;
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;

    @Transactional
    public BuyBookResponse BuyBook (BuyBookRequest request , Principal principal){
        String username = principal.getName().toLowerCase();
        log.info(username + " is buying the book : "+request.title());
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer Not Found : "+username));
        Book book = bookRepository.findBookByTitle(request.title())
                .orElseThrow(()-> new BookNotFoundException("Book Not Found : "+request.title()));
        if(book.getBookStatus().equals(BookStatus.OUT_OF_STOCKS)){
            throw new BookOutOfStocksException("Unfortunately Book is out of stocks for now.");
        }

        SoldBook soldBook = SoldBook.builder()
                .book(book)
                .buyer(customer)
                .isbn(book.getIsbn())
                .saleDate(LocalDateTime.now())
                .salePrice(book.getPrice())
                .title(book.getTitle())
                .build();

        book.setQuantity(book.getQuantity()-1);
        book.setTotal_sold(book.getTotal_sold()+1);
        customer.setTotalBoughtBook(customer.getTotalBoughtBook()+1);
        soldBookRepository.save(soldBook);
        customerRepository.save(customer);
        bookRepository.save(book);

        return new BuyBookResponse(
                "Book Purchased Successfully",
                customer.getUsername(),
                book.getTitle(),
                book.getPrice(),
                soldBook.getSaleDate()
        );
    }

    @Transactional
    public List<SoldBookResponse> listPurchaseHistory(Principal principal){
        String username = principal.getName();
        log.info(username + "Admin is listing the Purchase history.. ");
        List<SoldBook> soldBooks = soldBookRepository.findAll();
        if(soldBooks.isEmpty()){
            throw new NoBooksSoldException("No Books Sold yet.");
        }
        soldBooks.sort(Comparator.comparing(SoldBook::getSaleDate).reversed());
       return soldBooks
               .stream()
                .map(soldBook -> new SoldBookResponse(
                        soldBook.getIsbn(),
                        soldBook.getBuyer().getUsername(),
                        soldBook.getTitle(),
                        soldBook.getSoldBookId(),
                        soldBook.getSaleDate(),
                        soldBook.getSalePrice()
                ))
                .toList();
    }

    public List<MostSellers> mostSellers (Principal principal){
        String username = principal.getName();
        log.info(username + " is listing the Most sellers.. ");

        List<Book> books = bookRepository.findAll();
        if(books.isEmpty()){
            throw new NoBooksSoldException("No Books Sold yet.");
        }
        books.sort(Comparator.comparing(Book::getTotal_sold).reversed());
        int limit = 5;
        return books.stream()
                .limit(limit)
                .map(book -> new MostSellers(
                        book.getAuthor(),
                        book.getTitle(),
                        book.getGenre(),
                        book.getPrice()
                ))
                .toList();
    }
    public List<MostSellers> mostSellersByGenre (Genre genre, Principal principal){
        String username = principal.getName();
        log.info(username + " is listing the Most sellers By Genre.. ");

        List<Book> books = bookRepository.findByGenre(genre);
        if(books.isEmpty()){
            throw new NoBooksSoldException("No Books Sold for genre : "+genre);
        }
        books.sort(Comparator.comparing(Book::getTotal_sold).reversed());
        int limit = 5;
        return books.stream()
                .limit(limit)
                .map(book -> new MostSellers(
                        book.getAuthor(),
                        book.getTitle(),
                        book.getGenre(),
                        book.getPrice()
                ))
                .toList();
    }

    public List<MostSellers> mostSellersOfAuthor (String author, Principal principal){
        String username = principal.getName();
        log.info(username + " is listing the Most sellers By Genre.. ");

        List<Book> books = bookRepository.findByAuthor(author);
        if(books.isEmpty()){
            throw new NoBooksSoldException("Author has no books yet: "+author);
        }
        books.sort(Comparator.comparing(Book::getTotal_sold).reversed());
        int limit = 5;
        return books.stream()
                .limit(limit)
                .map(book -> new MostSellers(
                        book.getAuthor(),
                        book.getTitle(),
                        book.getGenre(),
                        book.getPrice()
                ))
                .toList();
    }

}
