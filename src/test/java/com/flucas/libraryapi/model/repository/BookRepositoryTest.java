package com.flucas.libraryapi.model.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.flucas.libraryapi.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {
    
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro com o ISBN informado")
    public void shouldReturnTrueWhenIsbnExists() {
        String isbn = "123";
        Book book = Book.builder()
            .title("Livro teste")
            .author("Autor teste")
            .isbn(isbn).build();
        entityManager.persist(book);

        boolean exists = repository.existsByIsbn(isbn);
        Assertions.assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro com o ISBN informado")
    public void shouldReturnFalseWhenIsbnDoesntExists() {
        String isbn = "123";
        Book book = Book.builder()
            .title("Livro teste")
            .author("Autor teste")
            .isbn(isbn).build();
        entityManager.persist(book);

        boolean exists = repository.existsByIsbn("321");
        Assertions.assertThat(exists).isFalse();
    }
}
