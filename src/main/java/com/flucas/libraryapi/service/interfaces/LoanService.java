package com.flucas.libraryapi.service.interfaces;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.flucas.libraryapi.api.dto.LoanFilterDTO;
import com.flucas.libraryapi.model.entity.Loan;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filter, Pageable pageRequest);
}
