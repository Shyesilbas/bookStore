package com.serhat.bookstore.Repository;

import com.serhat.bookstore.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer,Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByPhone(String phone);

    @Query("SELECT c FROM Customer c WHERE LOWER(c.username) = LOWER(:username)")

    Optional<Customer> findByUsername(String username);


}
