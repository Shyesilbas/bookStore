package com.serhat.bookstore.Repository;

import com.serhat.bookstore.model.ReservedBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservedBookRepository extends JpaRepository<ReservedBook,Long> {
}
