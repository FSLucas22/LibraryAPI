package com.flucas.libraryapi.api.resource;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.flucas.libraryapi.api.dto.LoanDTO;
import com.flucas.libraryapi.api.dto.LoanFilterDTO;
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
        loanToSave.setLoanDate(LocalDate.now());
        var loan = loanService.save(loanToSave);
        return loan.getId();
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        var loan = loanService.getById(id).orElseThrow(
            () -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Loan not found for passed id"
            )
        );
        loan.setReturned(dto.returned());
        loanService.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest) {
        var result = loanService.find(dto, pageRequest);
        return new PageImpl<LoanDTO>(
            result.getContent()
            .stream()
            .map(loan -> {
                var loanDto = modelMapper.map(loan, LoanDTO.class);
                loanDto.setIsbn(loan.getBook().getIsbn());
                return loanDto;
            })
            .collect(Collectors.toList()), pageRequest, result.getTotalElements());
    }
}
