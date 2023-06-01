package com.flucas.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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

    public Book createValidBook() {
        return Book.builder()
            .title("title")
            .author("author")
            .isbn("123")
            .build();
    }

    @Test
    @DisplayName("Deve retornar true quando existe emprestimo de livro não retornado")
    public void shouldReturnTrueWhenBookIsNotReturned() {
    
        Book book = createValidBook();
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
    
        Book book = createValidBook();
        Loan loan = Loan.builder()
            .book(book)
            .customer("customer")
            .returned(true)
            .build();
        
        entityManager.persist(book);
        entityManager.persist(loan);
        assertThat(repository.existsByBookAndNotReturned(book)).isFalse();
    }
}