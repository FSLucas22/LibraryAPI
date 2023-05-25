package com.flucas.libraryapi.api.resource;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flucas.libraryapi.api.dto.LoanDTO;
import com.flucas.libraryapi.model.entity.Loan;
import com.flucas.libraryapi.service.interfaces.LoanService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final ModelMapper modelMapper;
    private final LoanService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDTO create(@RequestBody LoanDTO dto) {
        var loanToSave = modelMapper.map(dto, Loan.class);
        var loan = service.save(loanToSave);
        return modelMapper.map(loan, LoanDTO.class);
    }
}
