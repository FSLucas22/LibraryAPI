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
        book = createBook();
    }

    Book createBook() {
        return Book.builder()
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

    @Test
    @DisplayName("Repository deve salvar um livro")
    public void shouldSaveNewBook() {
        var savedBook = repository.save(book);
        Assertions.assertThat(savedBook.getId()).isNotNull();
        Assertions.assertThat(repository.findById(savedBook.getId())).isPresent();
    }

    @Test
    @DisplayName("Repository deve atualizar um livro")
    public void shouldUpdateBook() {
        
        entityManager.persist(book);
        var updatingBook = createBook();
        updatingBook.setId(book.getId());
        updatingBook.setTitle("Outro titulo");
        updatingBook.setAuthor("Outro autor");

        var updatedBook = repository.save(updatingBook);

        Assertions.assertThat(updatedBook.getId()).isEqualTo(updatingBook.getId());
        Assertions.assertThat(updatedBook.getTitle()).isEqualTo(updatingBook.getTitle());
        Assertions.assertThat(updatedBook.getAuthor()).isEqualTo(updatingBook.getAuthor());
        Assertions.assertThat(updatedBook.getIsbn()).isEqualTo(updatingBook.getIsbn());
    }

    @Test
    @DisplayName("Repository só deve atualizar o livro informado")
    public void shouldNotUpdateDifferentBook() {
        entityManager.persist(book);
        var differentBook = createBook();
        differentBook.setIsbn("321");
        entityManager.persist(differentBook);

        book.setTitle("Outro titulo");
        book.setAuthor("Outro autor");
        
        repository.save(book);
        var copyOfDifferentBook = repository.findById(differentBook.getId());
        Assertions.assertThat(copyOfDifferentBook.isPresent()).isTrue();
        Assertions.assertThat(copyOfDifferentBook.get().getId()).isEqualTo(differentBook.getId());
        Assertions.assertThat(copyOfDifferentBook.get().getTitle()).isEqualTo(differentBook.getTitle());
        Assertions.assertThat(copyOfDifferentBook.get().getAuthor()).isEqualTo(differentBook.getAuthor());
        Assertions.assertThat(copyOfDifferentBook.get().getIsbn()).isEqualTo(differentBook.getIsbn());
    }

    @Test
    @DisplayName("Repository deve excluir um livro")
    public void shouldDeleteBook() {
        entityManager.persist(book);
        repository.delete(book);
        Assertions.assertThat(repository.findById(book.getId()).isEmpty()).isTrue();
        
    }

    @Test
    @DisplayName("Repository somente deve excluir o livro informado")
    public void shouldNotDeleteDifferentBook() {
        var differentBook = createBook();
        differentBook.setIsbn("321");
        entityManager.persist(book);
        entityManager.persist(differentBook);
        repository.delete(book);
        Assertions.assertThat(repository.findById(differentBook.getId()).isPresent()).isTrue();
    }
}
