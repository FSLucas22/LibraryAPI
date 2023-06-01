package com.flucas.libraryapi.api.resource;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.flucas.libraryapi.api.dto.LoanDTO;
import com.flucas.libraryapi.api.dto.ReturnedLoanDTO;
import com.flucas.libraryapi.model.entity.Loan;
import com.flucas.libraryapi.service.interfaces.BookService;
import com.flucas.libraryapi.service.interfaces.LoanService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final ModelMapper modelMapper;
    private final LoanService loanService;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody @Valid LoanDTO dto) {
        if (bookService.getByIsbn(dto.getIsbn()).isEmpty()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Book not found for passed isbn");
        } 
        var loanToSave = modelMapper.map(dto, Loan.class);
        var loan = loanService.save(loanToSave);
        return loan.getId();
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        var loan = loanService.getById(id).get();
        loan.setReturned(dto.returned());
        loanService.update(loan);
    }
}
