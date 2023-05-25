package com.flucas.libraryapi.api.resource;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flucas.libraryapi.api.dto.LoanDTO;
import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.model.entity.Loan;
import com.flucas.libraryapi.service.interfaces.BookService;
import com.flucas.libraryapi.service.interfaces.LoanService;


@ExtendWith(SpringExtension.class)
@ContextConfiguration
@ActiveProfiles("test")
@WebMvcTest(LoanController.class)
public class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;

    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void shouldLoanBook() throws Exception {
        LoanDTO dto = LoanDTO.builder().isbn("123").customer("customer").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getByIsbn(dto.getIsbn())).willReturn(
            Optional.of(Book.builder()
                            .id(1L)
                            .isbn("isbn")
                            .build()));
        
        Loan loan = Loan.builder().id(10L).isbn("123").customer("customer").build();

        BDDMockito.given(loanService.save(any(Loan.class))).willReturn(loan);

        var request = MockMvcRequestBuilders.post(LOAN_API)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

        mvc
            .perform(request)
            .andExpect(status().isCreated())
            .andExpect(content().string("10"));
    }

    @Test
    @DisplayName("Não deve realizar emprestimo quando o livro não existe")
    public void shouldNotLoanInexistentBook() throws Exception {
        LoanDTO dto = LoanDTO.builder().isbn("123").customer("customer").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getByIsbn(dto.getIsbn())).willReturn(Optional.empty());

        var request = MockMvcRequestBuilders.post(LOAN_API)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

        mvc
            .perform(request)
            .andExpect(status().isNotFound());
    }
}
