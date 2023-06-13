package com.flucas.libraryapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.flucas.libraryapi.exceptions.BusinessException;
import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.model.repository.BookRepository;
import com.flucas.libraryapi.service.interfaces.BookService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    private BookService service;

    private Book book;

    @MockBean
    private BookRepository repository;

    public Book createValidBook() {
        return Book.builder()
            .isbn("123")
            .author("Fulano")
            .title("Livro teste")
            .build();
    }

    @BeforeEach
    public void setup() {
        service = new BookServiceImp(repository);
        book = createValidBook();
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

    @Test
    @DisplayName("Service deve retornar um livro pelo isbn")
    public void shouldReturnBookByIsbn() {
        Long id = 1L;
        book.setId(id);
        Mockito.when(repository.findByIsbn("123"))
            .thenReturn(Optional.of(book));
        var foundBook = service.getByIsbn("123");
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Service deve retornar empty quando o isbn não existe")
    public void shouldReturnEmptyWhenIsbnNotFound() {
        Mockito.when(repository.findByIsbn("123"))
            .thenReturn(Optional.empty());
        Assertions.assertThat(service.getByIsbn("123")).isEmpty();
    }

    @Test
    @DisplayName("Deve excluir um livro")
    public void serviceShouldDeleteBook() {
        book.setId(1L);

        service.delete(book);

        verify(repository).delete(book);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao passar livro ou id nulo")
    public void serviceShouldThrowExpectionWhenDeletingInvalidBook() {
        book.setId(null);
        Throwable nullBookException = Assertions.catchThrowable(() -> service.delete(null));
        Throwable nullIdException = Assertions.catchThrowable(() -> service.delete(book));

        Assertions.assertThat(nullBookException)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Id do livro não pode ser nulo.");

        Assertions.assertThat(nullIdException)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Id do livro não pode ser nulo.");

        Mockito.verify(repository, never()).delete(any(Book.class));
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void serviceShouldUpdateBook() {
        long id = 1L;
        String valorParaAtualizar = "Outro autor";
        book.setId(id);
        var savedBook = createValidBook();
        savedBook.setId(id);
        savedBook.setAuthor(valorParaAtualizar);

        when(repository.save(book)).thenReturn(savedBook);
        book.setAuthor(valorParaAtualizar);
        
        var updatedBook = service.update(book);
        Assertions.assertThat(updatedBook.getId()).isEqualTo(savedBook.getId());
        Assertions.assertThat(updatedBook.getAuthor()).isEqualTo(savedBook.getAuthor());
        Assertions.assertThat(updatedBook.getTitle()).isEqualTo(savedBook.getTitle());
        Assertions.assertThat(updatedBook.getIsbn()).isEqualTo(savedBook.getIsbn());
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao passar livro ou id nulo")
    public void serviceShouldThrowExpectionWhenUpdatingInvalidBook() {
        book.setId(null);
        Throwable nullBookException = Assertions.catchThrowable(() -> service.update(null));
        Throwable nullIdException = Assertions.catchThrowable(() -> service.update(book));

        Assertions.assertThat(nullBookException)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Id do livro não pode ser nulo.");

        Assertions.assertThat(nullIdException)
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Id do livro não pode ser nulo.");

        Mockito.verify(repository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void shouldFindBookByFilters() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> bookList = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(
            bookList, pageRequest, 1);

        when(repository.findAll(ArgumentMatchers.<Example<Book>>any(), any(PageRequest.class)))
            .thenReturn(page);

        var result = service.find(book, pageRequest);
        Assertions.assertThat(result.getTotalElements()).isEqualTo(1);
        Assertions.assertThat(result.getContent()).isEqualTo(bookList);
        Assertions.assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        Assertions.assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
}