package com.flucas.libraryapi.api.service;

import org.springframework.stereotype.Service;
import com.flucas.libraryapi.api.entity.Book;
import com.flucas.libraryapi.api.repository.BookRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookServiceImp implements BookService {
    private BookRepository repository;

    @Override
    public Book save(Book book) {
        return repository.save(book);
    }
}
