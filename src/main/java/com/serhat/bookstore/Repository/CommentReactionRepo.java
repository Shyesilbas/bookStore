package com.serhat.bookstore.Repository;

import com.serhat.bookstore.model.Comment;
import com.serhat.bookstore.model.CommentReaction;
import com.serhat.bookstore.model.Customer;
import com.serhat.bookstore.model.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentReactionRepo extends JpaRepository<CommentReaction,Long> {
    Optional<CommentReaction> findByCustomerAndCommentAndReaction(Customer customer, Comment comment, Reaction reaction);

    boolean existsByCustomerAndCommentAndReaction(Customer customer, Comment comment, Reaction reaction);

    Optional<CommentReaction> findByCustomerAndComment(Customer customer, Comment comment);
}
