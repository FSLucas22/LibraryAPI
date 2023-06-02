package com.flucas.libraryapi.service.interfaces;

import java.util.Optional;

import com.flucas.libraryapi.model.entity.Loan;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}
