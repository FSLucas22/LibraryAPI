package com.flucas.libraryapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.flucas.libraryapi.api.entity.Book;
import com.flucas.libraryapi.api.repository.BookRepository;
import com.flucas.libraryapi.api.service.BookService;
import com.flucas.libraryapi.api.service.BookServiceImp;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    private BookService service;

    @MockBean
    private BookRepository repository;

    @BeforeEach
    public void setup() {
        service = new BookServiceImp(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    void shouldSaveBook() {
        var book = Book.builder()
                .isbn("123")
                .author("Fulano")
                .title("Livro teste")
                .build();

        var savedBook = Book
                .builder()
                .id(10L)
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .build();

        BDDMockito
                .given(repository.save(Mockito.any(Book.class)))
                .willReturn(savedBook);
        
        assertEquals(savedBook, service.save(book));
    }
}
