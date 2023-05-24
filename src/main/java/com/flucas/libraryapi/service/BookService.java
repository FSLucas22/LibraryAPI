package com.flucas.libraryapi.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.flucas.libraryapi.model.entity.Book;

public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book any);

    Book update(Book book);

    Page<Book> find(Book filter, Pageable pageRequest);
}
