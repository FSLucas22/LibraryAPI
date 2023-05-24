package com.flucas.libraryapi.api.resource;


import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.flucas.libraryapi.api.dto.BookDTO;
import com.flucas.libraryapi.api.exceptions.ApiErrors;
import com.flucas.libraryapi.exceptions.BusinessException;
import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.service.BookService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService service;
    private final ModelMapper modelMapper;

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

    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id) {
        return service.getById(id)
                    .map(b -> modelMapper.map(b, BookDTO.class))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        var book = service.getById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO update(@PathVariable Long id, @RequestBody @Valid BookDTO dto) {
        return service.getById(id)
            .map(book -> {
                book.setAuthor(dto.getAuthor());
                book.setTitle(dto.getTitle());
                return modelMapper.map(service.update(book), BookDTO.class);
            })
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<BookDTO> find(BookDTO dto, Pageable pageRequest) {
        Book filter = modelMapper.map(dto, Book.class);
        var result = service.find(filter, pageRequest);
        return new PageImpl<BookDTO>(
            result.getContent()
            .stream()
            .map(book -> modelMapper.map(book, BookDTO.class))
            .collect(Collectors.toList()), pageRequest, result.getTotalElements());
    }
}