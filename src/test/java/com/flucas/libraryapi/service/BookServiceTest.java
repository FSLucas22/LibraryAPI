package com.flucas.libraryapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.flucas.libraryapi.exceptions.BusinessException;
import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.model.repository.BookRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    private BookService service;

    private Book book;

    @MockBean
    private BookRepository repository;

    @BeforeEach
    public void setup() {
        service = new BookServiceImp(repository);
        book = Book.builder()
            .isbn("123")
            .author("Fulano")
            .title("Livro teste")
            .build();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void shouldSaveBook() {
        var savedBook = Book
                .builder()
                .id(10L)
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .build();

        BDDMockito
                .given(repository.existsByIsbn(book.getIsbn()))
                .willReturn(false);

        BDDMockito
                .given(repository.save(Mockito.any(Book.class)))
                .willReturn(savedBook);
        
        assertEquals(savedBook, service.save(book));
    }

    @Test
    @DisplayName("Service deve lançar erro ao tentar cadastrar ISBN duplicado")
    public void shouldNotAcceptDuplicateIsbn() {
        BDDMockito
                .given(repository.existsByIsbn(book.getIsbn()))
                .willReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(book));
        Assertions.assertThat(exception)
            .isInstanceOf(BusinessException.class)
            .hasMessage("ISBN já cadastrado.");
        
        Mockito.verify(repository, Mockito.never()).save(book);
    }
}
