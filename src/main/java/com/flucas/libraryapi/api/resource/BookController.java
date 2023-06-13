package com.flucas.libraryapi.api.resource;


import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.flucas.libraryapi.api.dto.LoanDTO;
import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.service.interfaces.BookService;
import com.flucas.libraryapi.service.interfaces.LoanService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Library API", description = "API de registro de livros")
@Slf4j
public class BookController {

    private final BookService service;
    private final ModelMapper modelMapper;
    private final LoanService loanService;

    @Operation(summary = "Create a new book")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        var book = modelMapper.map(dto, Book.class);
        var createdBook = service.save(book);
        log.info("Created book for isbn {}", dto.getIsbn());
        return modelMapper.map(createdBook, BookDTO.class);
    }

    @Operation(summary = "Get details about a created book by id")
    @GetMapping("{id}")
    public BookDTO get(@PathVariable Long id) {
        log.info("Obtaining details for book id {}", id);
        return service.getById(id)
                    .map(b -> modelMapper.map(b, BookDTO.class))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Delete a created book by id")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Book successfuly deleted")
    })
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Deleting book of id {}", id);
        var book = service.getById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }

    @Operation(summary = "Update a created book")
    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO update(@PathVariable Long id, @RequestBody @Valid BookDTO dto) {
        log.info("Updating book of id {}", id);
        return service.getById(id)
            .map(book -> {
                book.setAuthor(dto.getAuthor());
                book.setTitle(dto.getTitle());
                return modelMapper.map(service.update(book), BookDTO.class);
            })
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Filter books by params")
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

    @Operation(summary = "Filter loans of a book")
    @GetMapping("{id}/loans")
    public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageable) {
        var book = service.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var result = loanService.getLoansByBook(book, pageable);
        return new PageImpl<LoanDTO>(result.getContent().stream()
            .map(loan -> {
                var loanDto = modelMapper.map(loan, LoanDTO.class);
                loanDto.setIsbn(book.getIsbn());
                return loanDto;
            })
            .collect(Collectors.toList()), pageable, result.getTotalElements());
    }
}