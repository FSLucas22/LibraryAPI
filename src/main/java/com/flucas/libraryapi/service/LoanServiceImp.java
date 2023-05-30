package com.flucas.libraryapi.service;

import com.flucas.libraryapi.model.entity.Loan;
import com.flucas.libraryapi.model.repository.LoanRepository;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class LoanServiceImp {
    private final LoanRepository repository;

    public Loan save(Loan loan) {
        return repository.save(loan);
    }
}
