package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.BookRepository;
import com.serhat.bookstore.Repository.CommentRepository;
import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.exception.*;
import com.serhat.bookstore.model.Book;
import com.serhat.bookstore.model.Comment;
import com.serhat.bookstore.model.Customer;
import com.serhat.bookstore.model.Reaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;

    public String getUsername(Principal principal){
        return principal.getName().toLowerCase();
    }

    @Transactional
    public PostCommentResponse postComment (PostCommentRequest request , Principal principal){
        String username = getUsername(principal);
        Book book = bookRepository.findByIsbn(request.isbn())
                .orElseThrow(()-> new BookNotFoundException("Book Not Found : "+request.isbn()));
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found : "+username));

        Comment comment = Comment.builder()
                .book(book)
                .customer(customer)
                .comment(request.comment())
                .rate(request.rate())
                .reaction(Reaction.COMMENT)
                .likes(0)
                .dislikes(0)
                .repost(0)
                .build();

        if (request.rate() < 0 || request.rate() > 5) {
            throw new IllegalRatingException("Rate must be between 0 and 5.");
        }

        customer.setTotalComments(customer.getTotalComments()+1);
        book.setTotalComments(book.getTotalComments()+1);
        commentRepository.save(comment);
        customerRepository.save(customer);
        bookRepository.save(book);

        return new PostCommentResponse(
          "Comment Posted Successfully",
                    username,
                    request.rate(),
                    request.comment()
        );
    }

    public List<HighestRatedCommentsForBookResponse> highestRatedCommentsForBook (String title , Principal principal){
        String username = principal.getName().toLowerCase();
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found : "+username));
        Book book = bookRepository.findByTitle(title)
                .orElseThrow(()-> new BookNotFoundException("Book Not Found : "+title));
        List<Comment> comments = commentRepository.findByBook(book);
        if(comments.isEmpty()){
            throw new NoCommentFoundForBookException("No comment found for this Book");
        }
        comments.sort(Comparator.comparing(Comment::getRate).reversed());
        int limit = 5;
        return comments.stream()
                .limit(limit)
                .map(comment -> new HighestRatedCommentsForBookResponse(
                        comment.getCustomer().getUsername(),
                        comment.getComment(),
                        comment.getLikes(),
                        comment.getDislikes(),
                        comment.getRepost(),
                        comment.getRate()
                ))
                .toList();
    }

    public List<leastRatedCommentsForBookResponse> leastRatedCommentsForBook (String title , Principal principal){
        String username = principal.getName().toLowerCase();
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found : "+username));
        Book book = bookRepository.findByTitle(title)
                .orElseThrow(()-> new BookNotFoundException("Book Not Found : "+title));
        List<Comment> comments = commentRepository.findByBook(book);
        if(comments.isEmpty()){
            throw new NoCommentFoundForBookException("No comment found for this Book");
        }
        comments.sort(Comparator.comparing(Comment::getRate));
        int limit = 5;
        return comments.stream()
                .limit(limit)
                .map(comment -> new leastRatedCommentsForBookResponse(
                        comment.getCustomer().getUsername(),
                        comment.getComment(),
                        comment.getLikes(),
                        comment.getDislikes(),
                        comment.getRepost(),
                        comment.getRate()
                ))
                .toList();
    }


}
