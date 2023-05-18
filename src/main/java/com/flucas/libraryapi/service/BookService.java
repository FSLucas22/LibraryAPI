package com.flucas.libraryapi.service;

import java.util.Optional;
import com.flucas.libraryapi.model.entity.Book;

public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book any);
}
