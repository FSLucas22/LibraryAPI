package com.flucas.libraryapi.service;

import com.flucas.libraryapi.api.entity.Book;
import com.flucas.libraryapi.api.service.BookService;
import com.flucas.libraryapi.api.service.BookServiceImp;
import com.flucas.libraryapi.api.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    private BookService service;

    private BookRepository repository;

    @BeforeEach
    public void setup() {
        service = new BookServiceImp();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    void shouldSaveBook() {
        var book = Book.builder()
                .isbn("123")
                .author("Fulano")
                .title("Livro teste")
                .build();

        var savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
    }
}
