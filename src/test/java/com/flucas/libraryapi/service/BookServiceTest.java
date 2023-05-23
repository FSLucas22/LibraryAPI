package com.flucas.libraryapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.Optional;

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

    @Test
    @DisplayName("Service deve retornar um livro pelo id")
    public void shouldReturnBookById() {
        long id = 1L;
        book.setId(id);
        Mockito.when(repository.findById(id))
            .thenReturn(Optional.of(book));
        var foundBook = service.getById(id);
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Service deve retornar empty quando o id não existe")
    public void shouldReturnEmptyWhenIdNotFound() {
        Mockito.when(repository.findById(anyLong()))
            .thenReturn(Optional.empty());
        Assertions.assertThat(service.getById(1L).isEmpty()).isTrue();
    }
}
