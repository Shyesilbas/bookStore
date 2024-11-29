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

    @Transactional
    public LikeResponse likeComment(LikeRequest request, Principal principal) {
        String username = getUsername(principal);
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + username));
        Comment comment = commentRepository.findById(request.commentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found: " + request.commentId()));

        Customer commentAuthor = comment.getCustomer();

        boolean alreadyLiked = commentRepository.existsByCustomerAndCommentIdAndReaction(customer, comment.getCommentId(), Reaction.LIKED);
        boolean alreadyDisliked = commentRepository.existsByCustomerAndCommentIdAndReaction(customer, comment.getCommentId(), Reaction.DISLIKED);

        if (alreadyLiked) {
            processReactionCounters(customer, commentAuthor, comment, -1, Reaction.LIKE_CANCELLED);
            log.info("Like cancelled for comment: " + comment.getCommentId());
            return new LikeResponse("Like is Cancelled", customer.getUsername(), comment.getComment());
        }

        if (alreadyDisliked) {
            processReactionCounters(customer, commentAuthor, comment, -1, Reaction.DISLIKE_CANCELLED);
            log.info("Dislike cancelled, changing to like for comment: " + comment.getCommentId());
        }

        processReactionCounters(customer, commentAuthor, comment, 1, Reaction.LIKED);
        log.info("Like added for comment: " + comment.getCommentId());

        return new LikeResponse("Message liked successfully", customer.getUsername(), comment.getComment());
    }

    @Transactional
    public DislikeResponse dislikeComment(DislikeRequest request, Principal principal) {
        String username = getUsername(principal);
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found: " + username));
        Comment comment = commentRepository.findById(request.commentId())
                .orElseThrow(() -> new CommentNotFoundException("Comment not found: " + request.commentId()));

        Customer commentAuthor = comment.getCustomer();

        boolean alreadyDisliked = commentRepository.existsByCustomerAndCommentIdAndReaction(customer, comment.getCommentId(), Reaction.DISLIKED);
        boolean alreadyLiked = commentRepository.existsByCustomerAndCommentIdAndReaction(customer, comment.getCommentId(), Reaction.LIKED);

        if (alreadyDisliked) {
            processReactionCounters(customer, commentAuthor, comment, -1, Reaction.DISLIKE_CANCELLED);
            log.info("Dislike cancelled for comment: " + comment.getCommentId());
            return new DislikeResponse("Dislike is Cancelled", customer.getUsername(), comment.getComment());
        }

        if (alreadyLiked) {
            processReactionCounters(customer, commentAuthor, comment, -1, Reaction.LIKE_CANCELLED);
            log.info("Like cancelled, changing to dislike for comment: " + comment.getCommentId());
        }

        processReactionCounters(customer, commentAuthor, comment, 1, Reaction.DISLIKED);
        log.info("Dislike added for comment: " + comment.getCommentId());

        return new DislikeResponse("Dislike saved Successfully", customer.getUsername(), comment.getComment());
    }

    @Transactional
    public RepostResponse repostComment(RepostRequest request , Principal principal){
        String username = getUsername(principal);
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(()-> new CustomerNotFoundException("Customer not found"+username));
        log.info("Customer reposted : "+username);

        Comment comment = commentRepository.findById(request.commentId())
                .orElseThrow(()-> new CommentNotFoundException("Comment not found : "+request.commentId()));

        Customer commentAuthor = comment.getCustomer();
        log.info("Comment author : "+commentAuthor.getUsername());

        boolean alreadyReposted = commentRepository.existsByCustomerAndCommentIdAndReaction(customer, comment.getCommentId(), Reaction.REPOSTED);

        if (alreadyReposted) {
            processReactionCounters(customer, commentAuthor, comment, -1, Reaction.REPOST_CANCELLED);
                log.info("Repost cancelled for comment: " + comment.getCommentId());
            return new RepostResponse("Repost is Cancelled", customer.getUsername(), comment.getComment());
        }

        processReactionCounters(customer, commentAuthor, comment, 1, Reaction.REPOSTED);
        log.info("Repost made for comment: " + comment.getCommentId());

        return new RepostResponse("Repost saved Successfully", customer.getUsername(), comment.getComment());
    }

    @Transactional
    public void processReactionCounters(Customer customer, Customer commentAuthor, Comment comment, int value, Reaction reaction) {
        switch (reaction) {
            case DISLIKED, DISLIKE_CANCELLED -> {
                customer.setTotalDislikes(customer.getTotalDislikes() + value);
                commentAuthor.setTotalDislikesReceived(commentAuthor.getTotalDislikesReceived() + value);
                comment.setDislikes(comment.getDislikes() + value);
            }
            case LIKED, LIKE_CANCELLED -> {
                customer.setTotalLikes(customer.getTotalLikes() + value);
                commentAuthor.setTotalLikesReceived(commentAuthor.getTotalLikesReceived() + value);
                comment.setLikes(comment.getLikes() + value);
            }
            case REPOSTED, REPOST_CANCELLED -> {
                customer.setTotalReposts(customer.getTotalReposts() + value);
                commentAuthor.setTotalRepostsReceived(commentAuthor.getTotalRepostsReceived() + value);
                comment.setRepost(comment.getRepost() + value);
            }
        }
        comment.setReaction(reaction);

        customerRepository.save(customer);
        customerRepository.save(commentAuthor);
        commentRepository.save(comment);
    }



}
