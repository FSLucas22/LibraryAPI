package com.flucas.libraryapi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flucas.libraryapi.api.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {
}
