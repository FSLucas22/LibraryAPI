package com.flucas.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.flucas.libraryapi.exceptions.BusinessException;
import com.flucas.libraryapi.model.entity.Loan;
import com.flucas.libraryapi.model.repository.LoanRepository;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    LoanRepository repository;

    public Loan createValidLoan(Long id) {
        return Loan.builder().id(id).customer("Customer").isbn("123").build();
    }

    @Test
    @DisplayName("Service deve salvar um empréstimo")
    public void shouldSaveLoan() {
        var loan = createValidLoan(null);
        var service = new LoanServiceImp(repository);
        var savedLoan = createValidLoan(1L);
        when(repository.save(loan)).thenReturn(savedLoan);
        var savingLoan = service.save(loan);
        assertThat(savingLoan.getId()).isEqualTo(savedLoan.getId());
        assertThat(savingLoan.getIsbn()).isEqualTo(savedLoan.getIsbn());
        assertThat(savingLoan.getCustomer()).isEqualTo(savedLoan.getCustomer());
    }

    @Test
    @DisplayName("Deve lançar BusinessException ao tentar salvar empréstimo de livro já emprestado")
    public void shouldThrowBusinessExceptionOnSavingLoanedBook() {
        var loan = createValidLoan(null);
        var service = new LoanServiceImp(repository);
        when(repository.existsByIsbn(loan.getIsbn())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(loan));

        assertThat(exception)
            .isInstanceOf(BusinessException.class)
            .hasMessage("Book already loaned");
    }
}