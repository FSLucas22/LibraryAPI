package com.flucas.libraryapi.api.service;

import com.flucas.libraryapi.api.entity.Book;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImp implements BookService {

    @Override
    public Book save(Book book) {
        var modelMapper = new ModelMapper();
        var savedBook = modelMapper.map(book, Book.class);
        savedBook.setId(10L);
        return savedBook;
    }
}
