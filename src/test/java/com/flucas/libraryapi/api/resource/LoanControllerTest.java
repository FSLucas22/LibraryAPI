package com.flucas.libraryapi.api.resource;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flucas.libraryapi.api.dto.LoanDTO;
import com.flucas.libraryapi.api.dto.LoanFilterDTO;
import com.flucas.libraryapi.api.dto.ReturnedLoanDTO;
import com.flucas.libraryapi.exceptions.BusinessException;
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

    private LoanDTO dto;

    @BeforeEach
    public void setUp() {
        this.dto = LoanDTO.builder().isbn("123").customer("customer").build();
    }

    public Book createBook(Long id, String isbn) {
        return Book.builder()
            .id(id)
            .isbn(isbn)
            .build();
    }

    public Loan createLoan(Long id, String customer, Book book) {
        return Loan.builder().id(id).book(book).customer(customer).build();
    }

    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void shouldLoanBook() throws Exception {
        String isbn = "123";
        String customer = "customer";
        Book book = createBook(1L, isbn);
        Loan loan = createLoan(10L, customer, book);

        String json = new ObjectMapper().writeValueAsString(dto);

        given(bookService.getByIsbn(dto.getIsbn())).willReturn(
            Optional.of(book));

        given(loanService.save(any(Loan.class))).willReturn(loan);

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
    @DisplayName("Deve retornar BAD REQUEST quando o livro não existe")
    public void shouldNotLoanInexistentBook() throws Exception {
        String json = new ObjectMapper().writeValueAsString(dto);

        given(bookService.getByIsbn(dto.getIsbn())).willReturn(Optional.empty());

        var request = MockMvcRequestBuilders.post(LOAN_API)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);

        mvc
            .perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errors", Matchers.hasSize(1)))
            .andExpect(jsonPath("errors[0]")
                        .value("Book not found for passed isbn"));
    }

    @Test
    @DisplayName("Deve retornar BAD REQUEST quando não houver dados o suficiente")
    public void shouldOnlySaveValidLoans() throws Exception {
        LoanDTO dto = LoanDTO.builder().build();
        String json = new ObjectMapper().writeValueAsString(dto);
        var request = MockMvcRequestBuilders.post(LOAN_API)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);
        
        mvc
            .perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errors", Matchers.hasSize(2)));
    }

    @Test
    @DisplayName("Deve retornar BAD REQUEST quando o livro já estiver emprestado")
    public void shouldNotLoanSameBookTwice() throws Exception {
        String isbn = "123";
        String json = new ObjectMapper().writeValueAsString(dto);

        given(bookService.getByIsbn(dto.getIsbn())).willReturn(
            Optional.of(createBook(1L, isbn)));

        given(loanService.save(any(Loan.class)))
            .willThrow(new BusinessException("Book already loaned"));

        var request = MockMvcRequestBuilders
            .post(LOAN_API)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json);
        
        mvc
            .perform(request)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("errors", Matchers.hasSize(1)))
            .andExpect(jsonPath("errors[0]").value("Book already loaned"));
    }

    @Test
    @DisplayName("Deve retornar um livro emprestado")
    public void shouldReturnABook() throws Exception {
        var returnedDto = new ReturnedLoanDTO(true);
        var json = new ObjectMapper().writeValueAsString(returnedDto);

        Loan loan = createLoan(1L, "customer", createBook(10L, "123"));

        given(loanService.getById(1L)).willReturn(
            Optional.of(loan));
            
        mvc
            .perform(
                patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
            ).andExpect(status().isOk());

        verify(loanService).update(loan);
    }

    @Test
    @DisplayName("Deve retornar BAD REQUEST quando o emprestimo é inexistente")
    public void shouldNotReturnInexistentBook() throws Exception {
        var returnedDto = new ReturnedLoanDTO(true);
        var json = new ObjectMapper().writeValueAsString(returnedDto);

        given(loanService.getById(1L)).willReturn(
            Optional.empty());
            
        mvc
            .perform(
                patch(LOAN_API.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("errors", hasSize(1)))
            .andExpect(jsonPath("errors[0]").value("Loan not found for passed id"));

        verify(loanService, never()).update(any(Loan.class));
    }

    @Test
    @DisplayName("Controller deve filtrar empréstimos")
    public void shouldFilterBooks() throws Exception {
        long id = 1L;
        var filterDto = new LoanFilterDTO("123", "customer");

        var loan = createLoan(id, "customer", createBook(10L, "123"));

        given(loanService.find(any(LoanFilterDTO.class), any(Pageable.class)))
                  .willReturn(
                        new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0, 100), 
                        1));

        String query = String.format("?isbn=%s&customer=%s&page=0&size=100", 
                                     filterDto.isbn(), filterDto.customer());
        
        var request = MockMvcRequestBuilders.get(LOAN_API.concat(query))
                .accept(MediaType.APPLICATION_JSON);
        
        mvc
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", Matchers.hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }
}
