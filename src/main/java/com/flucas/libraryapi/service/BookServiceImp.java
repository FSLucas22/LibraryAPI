package com.flucas.libraryapi.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.flucas.libraryapi.exceptions.BusinessException;
import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.model.repository.BookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImp implements BookService {
    private final BookRepository repository;

    @Override
    public Book save(Book book) {
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new BusinessException("ISBN já cadastrado.");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void delete(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Id do livro não pode ser nulo.");
        }
        repository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if (book == null || book.getId() == null) {
            throw new IllegalArgumentException("Id do livro não pode ser nulo.");
        }
        return repository.save(book);
    }
}
