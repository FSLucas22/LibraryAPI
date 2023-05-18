package com.flucas.libraryapi.service;

import org.springframework.stereotype.Service;

import com.flucas.libraryapi.exceptions.BusinessException;
import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.model.repository.BookRepository;

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
