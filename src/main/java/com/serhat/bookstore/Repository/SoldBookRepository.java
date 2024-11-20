package com.serhat.bookstore.Repository;

import com.serhat.bookstore.model.SoldBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SoldBookRepository extends JpaRepository<SoldBook,Long> {
}
