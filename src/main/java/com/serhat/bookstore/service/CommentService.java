package com.serhat.bookstore.service;

import com.serhat.bookstore.Repository.BookRepository;
import com.serhat.bookstore.Repository.CommentRepository;
import com.serhat.bookstore.Repository.CustomerRepository;
import com.serhat.bookstore.dto.*;
import com.serhat.bookstore.exception.AlreadyDislikedException;
import com.serhat.bookstore.exception.BookNotFoundException;
import com.serhat.bookstore.exception.CommentNotFoundException;
import com.serhat.bookstore.exception.CustomerNotFoundException;
import com.serhat.bookstore.model.Book;
import com.serhat.bookstore.model.Comment;
import com.serhat.bookstore.model.Customer;
import com.serhat.bookstore.model.Reaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public PostCommentResponse postComment (PostCommentRequest request , Principal principal){
        String username = principal.getName().toLowerCase();
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

    @Transactional
    public DislikeResponse dislikeComment(DislikeRequest request , Principal principal){
        String username = principal.getName().toLowerCase();

        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found"+username));
        log.info("Customer disliked : "+username);

        Comment comment = commentRepository.findById(request.commentId())
                .orElseThrow(()-> new CommentNotFoundException("Comment not found : "+request.commentId()));

        boolean alreadyDisliked = commentRepository.existsByCustomerAndCommentIdAndReaction(customer, comment.getCommentId(), Reaction.DISLIKED);

        if(alreadyDisliked){
            throw new AlreadyDislikedException("You Already disliked this comment");
        }

        Customer commentAuthor = comment.getCustomer();
        log.info("Comment author : "+commentAuthor.getUsername());

        customer.setTotalDislikes(customer.getTotalDislikes()+1);
        commentAuthor.setTotalDislikesReceived(commentAuthor.getTotalDislikesReceived()+1);
        comment.setDislikes(comment.getDislikes()+1);
        customerRepository.save(customer);
        customerRepository.save(commentAuthor);
        commentRepository.save(comment);

        return new DislikeResponse(
                "Message Disliked Successfully",
                customer.getUsername(),
                comment.getComment()
        );
    }
    @Transactional
    public LikeResponse likeComment(LikeRequest request , Principal principal){
        String username = principal.getName().toLowerCase();

        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found"+username));
        log.info("Customer liked : "+username);

        Comment comment = commentRepository.findById(request.commentId())
                .orElseThrow(()-> new CommentNotFoundException("Comment not found : "+request.commentId()));

        Customer commentAuthor = comment.getCustomer();
        log.info("Comment author : "+commentAuthor.getUsername());

        boolean alreadyLiked = commentRepository.existsByCustomerAndCommentIdAndReaction(customer, comment.getCommentId(), Reaction.LIKED);

        if(alreadyLiked){
            throw new AlreadyDislikedException("You Already Liked this comment");
        }

        customer.setTotalLikes(customer.getTotalLikes()+1);
        commentAuthor.setTotalLikesReceived(commentAuthor.getTotalLikesReceived()+1);
        comment.setLikes(comment.getLikes()+1);
        customerRepository.save(customer);
        customerRepository.save(commentAuthor);
        commentRepository.save(comment);

        return new LikeResponse(
                "Message Liked Successfully",
                customer.getUsername(),
                comment.getComment()
        );
    }

    @Transactional
    public RepostResponse repostComment(RepostRequest request , Principal principal){
        String username = principal.getName().toLowerCase();

        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found"+username));
        log.info("Customer reposted : "+username);

        Comment comment = commentRepository.findById(request.commentId())
                .orElseThrow(()-> new CommentNotFoundException("Comment not found : "+request.commentId()));

        Customer commentAuthor = comment.getCustomer();
        log.info("Comment author : "+commentAuthor.getUsername());

        boolean alreadyReposted = commentRepository.existsByCustomerAndCommentIdAndReaction(customer, comment.getCommentId(), Reaction.REPOSTED);

        if(alreadyReposted){
            throw new AlreadyDislikedException("You Already Reposted this comment");
        }


        customer.setTotalReposts(customer.getTotalReposts()+1);
        commentAuthor.setTotalRepostsReceived(commentAuthor.getTotalRepostsReceived()+1);
        comment.setRepost(comment.getRepost()+1);
        customerRepository.save(customer);
        customerRepository.save(commentAuthor);
        commentRepository.save(comment);

        return new RepostResponse(
                "Message Reposted Successfully",
                customer.getUsername(),
                comment.getComment()
        );
    }

}
