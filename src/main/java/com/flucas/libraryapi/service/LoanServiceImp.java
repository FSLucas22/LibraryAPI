package com.flucas.libraryapi.service;

import java.util.Optional;

import com.flucas.libraryapi.exceptions.BusinessException;
import com.flucas.libraryapi.model.entity.Loan;
import com.flucas.libraryapi.model.repository.LoanRepository;
import com.flucas.libraryapi.service.interfaces.LoanService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
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
}
