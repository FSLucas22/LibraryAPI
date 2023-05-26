package com.flucas.libraryapi.api.resource;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flucas.libraryapi.api.dto.LoanDTO;
import com.flucas.libraryapi.api.exceptions.ApiErrors;
import com.flucas.libraryapi.exceptions.BusinessException;
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
            throw new BusinessException("Book not found for passed isbn");
        } 
        var loanToSave = modelMapper.map(dto, Loan.class);
        var loan = loanService.save(loanToSave);
        return loan.getId();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException exception) {
        return new ApiErrors(exception.getBindingResult());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessExceptions(BusinessException exception) {
        return new ApiErrors(exception);
    }
}
