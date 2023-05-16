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

import com.flucas.libraryapi.api.dto.BookDTO;
import com.flucas.libraryapi.api.entity.Book;
import com.flucas.libraryapi.api.exceptions.ApiErrors;
import com.flucas.libraryapi.api.service.BookService;
import com.flucas.libraryapi.exceptions.BusinessException;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/api/books")
@AllArgsConstructor
public class BookController {

    private BookService service;
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        var book = modelMapper.map(dto, Book.class);
        var createdBook = service.save(book);

        return modelMapper.map(createdBook, BookDTO.class);
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
