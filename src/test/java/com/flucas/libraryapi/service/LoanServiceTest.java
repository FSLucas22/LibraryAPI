package com.flucas.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.flucas.libraryapi.exceptions.BusinessException;
import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.model.entity.Loan;
import com.flucas.libraryapi.model.repository.LoanRepository;
import com.flucas.libraryapi.service.interfaces.LoanService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    LoanRepository repository;

    private LoanService service;
    
    @BeforeEach
    public void setUp() {
        service = new LoanServiceImp(repository);
    }

    public Loan createValidLoan(Long id, Book book) {
        return Loan.builder().id(id).book(book).customer("Customer").build();
    }

    public Book createBook(Long id, String isbn) {
        return Book.builder()
            .id(id)
            .isbn(isbn)
            .build();
    }

    @Test
    @DisplayName("Service deve salvar um empréstimo")
    public void shouldSaveLoan() {
        String isbn = "123";
        var book = createBook(10L, isbn);
        var loan = createValidLoan(null, book);
        var savedLoan = createValidLoan(1L, book);

        when(repository.save(loan)).thenReturn(savedLoan);
        var savingLoan = service.save(loan);

        assertThat(savingLoan.getId()).isEqualTo(savedLoan.getId());
        assertThat(savedLoan.getBook().getId()).isEqualTo(book.getId());
        assertThat(savingLoan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(savingLoan.getCustomer()).isEqualTo(savedLoan.getCustomer());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar salvar empréstimo de livro já emprestado")
    public void shouldThrowBusinessExceptionOnSavingLoanedBook() {
        String isbn = "123";
        var loan = createValidLoan(null, createBook(1L, isbn));
        when(repository.existsByBookAndNotReturned(loan.getBook())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(loan));

        assertThat(exception)
            .isInstanceOf(BusinessException.class)
            .hasMessage("Book already loaned");
    }

    @Test
    @DisplayName("Deve retornar detalhes de um empréstimo")
    public void shouldReturnLoanDetails() {
        long id = 1L;
        var loan = createValidLoan(id, createBook(10L, "123"));
        
        when(repository.findById(id)).thenReturn(Optional.of(loan));
        
        var gotLoan = service.getById(id);

        assertThat(gotLoan).isPresent();
        assertThat(gotLoan.get().getId()).isEqualTo(id);
        assertThat(gotLoan.get().getBook().getId()).isEqualTo(loan.getBook().getId());
        assertThat(gotLoan.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(gotLoan.get().getReturned()).isEqualTo(loan.getReturned());
    }

    @Test
    @DisplayName("Deve atualizar um empréstimo")
    public void shouldUpdateLoan() {
        long id = 1L;
        var loan = createValidLoan(id, createBook(10L, "123"));
        var savedLoan = createValidLoan(id, createBook(10L, "123"));
        savedLoan.setReturned(true);

        when(repository.save(loan)).thenReturn(savedLoan);
        
        var savingLoan = service.update(loan);

        assertThat(savingLoan.getId()).isEqualTo(savedLoan.getId());
        assertThat(savingLoan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(savingLoan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(savingLoan.getReturned()).isEqualTo(savedLoan.getReturned());
    }
}