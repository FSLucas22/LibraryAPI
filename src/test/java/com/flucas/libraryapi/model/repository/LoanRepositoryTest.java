package com.flucas.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.model.entity.Loan;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    LoanRepository repository;

    public Book createValidBook(String isbn) {
        return Book.builder()
            .title("title")
            .author("author")
            .isbn(isbn)
            .build();
    }

    public Loan createLoan(LocalDate date, Book book, Boolean returned) {
        return Loan.builder()
            .customer("customer")
            .book(book)
            .loanDate(date)
            .returned(returned).build();
    }

    @Test
    @DisplayName("Deve retornar true quando existe emprestimo de livro não retornado")
    public void shouldReturnTrueWhenBookIsNotReturned() {
    
        Book book = createValidBook("123");
        Loan loan = Loan.builder()
            .book(book)
            .customer("customer")
            .build();
        
        entityManager.persist(book);
        entityManager.persist(loan);
        assertThat(repository.existsByBookAndNotReturned(book)).isTrue();
    }

    @Test
    @DisplayName("Deve retornar true quando existe emprestimo de livro não retornado")
    public void shouldReturnFalseWhenBookIsReturned() {
    
        Book book = createValidBook("123");
        Loan loan = Loan.builder()
            .book(book)
            .customer("customer")
            .returned(true)
            .build();
        
        entityManager.persist(book);
        entityManager.persist(loan);
        assertThat(repository.existsByBookAndNotReturned(book)).isFalse();
    }

    @Test
    @DisplayName("Deve filtrar empréstimos")
    public void shouldFilterLoans() {
        var book = createValidBook("123");
        var loan = Loan.builder()
            .book(book)
            .customer("custumer")
            .build();
        PageRequest pageRequest = PageRequest.of(0, 10);
        entityManager.persist(book);
        entityManager.persist(loan);
        var result = repository.findByBookIsbnOrCustomer(
            book.getIsbn(), loan.getCustomer(), pageRequest);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

        var result_loan = result.getContent().get(0);
        assertThat(result_loan.getId()).isEqualTo(loan.getId());
        assertThat(result_loan.getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result_loan.getBook().getId()).isEqualTo(book.getId());
        assertThat(result_loan.getBook().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar os empréstimos atrasados")
    public void shouldReturnLateLoans() {
        var bookReturned = createValidBook("123");
        var bookOnDate = createValidBook("456");
        var bookLate = createValidBook("789");
        var loanReturned = createLoan(LocalDate.now().minusDays(6), bookReturned, true);
        var loanOnDate = createLoan(LocalDate.now().minusDays(3), bookOnDate, false);
        var loanLate = createLoan(LocalDate.now().minusDays(4), bookLate, null);

        entityManager.persist(bookReturned);
        entityManager.persist(bookOnDate);
        entityManager.persist(bookLate);
        entityManager.persist(loanReturned);
        entityManager.persist(loanOnDate);
        entityManager.persist(loanLate);

        var result = repository.findByLoanDateLessThanAndNotReturned(
            LocalDate.now().minusDays(4));

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId()).isEqualTo(loanLate.getId());
    }
}
