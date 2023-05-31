package com.flucas.libraryapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.model.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long>{
    boolean existsByBookAndNotReturned(Book book);
}
