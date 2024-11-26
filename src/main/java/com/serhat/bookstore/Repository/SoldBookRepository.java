package com.serhat.bookstore.Repository;

import com.serhat.bookstore.model.SoldBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoldBookRepository extends JpaRepository<SoldBook,Long> {
    List<SoldBook> findByBuyer_CustomerId(Long customerId);
}
