package com.serhat.bookstore.Repository;

import com.serhat.bookstore.model.Book;
import com.serhat.bookstore.model.Comment;
import com.serhat.bookstore.model.Customer;
import com.serhat.bookstore.model.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    boolean existsByCustomerAndCommentIdAndReaction(Customer customer, Long commentId, Reaction reaction);

    List<Comment> findByCustomer_Username(String username);


    List<Comment> findByCustomer(Customer customer);

    List<Comment> findByBook(Book book);
}
