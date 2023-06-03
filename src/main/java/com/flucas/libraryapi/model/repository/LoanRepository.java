package com.flucas.libraryapi.model.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.model.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long>{
    @Query(value = "select count(*) > 0 from Loan " +
        " where (returned is null or returned = false) and book = :book ") // returned is false não funciona para mim
    boolean existsByBookAndNotReturned(@Param("book") Book book);

    Page<Loan> findByBookIsbnOrCustomer(String isbn, String customer, Pageable pageRequest);

    Page<Loan> findByBook(Book book, Pageable pageabl);

    @Query(value = "select l from Loan l " +
                   "where (returned is null or returned = false) and loanDate <= :lateDate ")
    List<Loan> findByLoanDateLessThanAndNotReturned(@Param("lateDate") LocalDate lateDate);
}
