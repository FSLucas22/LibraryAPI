package com.flucas.libraryapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.flucas.libraryapi.api.dto.LoanFilterDTO;
import com.flucas.libraryapi.exceptions.BusinessException;
import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.model.entity.Loan;
import com.flucas.libraryapi.model.repository.LoanRepository;
import com.flucas.libraryapi.service.interfaces.LoanService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class LoanServiceImp implements LoanService {
    private final LoanRepository repository;

    public Loan save(Loan loan) {
        if (repository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Book already loaned");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filter, Pageable pageRequest) {
        return repository.findByBookIsbnOrCustomer(filter.isbn(), filter.customer(), pageRequest);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
        return repository.findByBook(book, pageable);
    }

    @Override
    public List<Loan> getAllLateLoans() {
        final Integer MAX_LOAN_DAYS = 4;
        LocalDate threeDaysAgo = LocalDate.now().minusDays(MAX_LOAN_DAYS);
        return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
    }
}
