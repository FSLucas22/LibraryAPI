package com.flucas.libraryapi.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

    Book book;

    @BeforeEach
    public void setUp() {
        book = Book.builder()
            .title("Livro teste")
            .author("Autor teste")
            .isbn("123").build();
    }

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro com o ISBN informado")
    public void shouldReturnTrueWhenIsbnExists() {
        entityManager.persist(book);
        Assertions.assertThat(repository.existsByIsbn(book.getIsbn())).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro com o ISBN informado")
    public void shouldReturnFalseWhenIsbnDoesntExists() {
        entityManager.persist(book);
        Assertions.assertThat(repository.existsByIsbn("321")).isFalse();
    }

    @Test
    @DisplayName("Repository deve retornar o livro pelo id")
    public void shouldFindBookById() {
        entityManager.persist(book);
        Optional<Book> foundBook = repository.findById(book.getId());
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Repository deve retornar empty quando o id não existe")
    public void shouldReturnEmptyWhenIdNotFound() {
        Assertions.assertThat(repository.findById(2L).isEmpty()).isTrue();
    }
}
