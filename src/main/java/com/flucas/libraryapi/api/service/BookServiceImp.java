package com.flucas.libraryapi.api.service;

import org.springframework.stereotype.Service;
import com.flucas.libraryapi.api.entity.Book;
import com.flucas.libraryapi.api.repository.BookRepository;
import com.flucas.libraryapi.exceptions.BusinessException;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookServiceImp implements BookService {
    private BookRepository repository;

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("ISBN já cadastrado.");
        }
        return repository.save(book);
    }
}
