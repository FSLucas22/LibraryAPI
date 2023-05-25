package com.flucas.libraryapi.api.resource;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flucas.libraryapi.api.dto.LoanDTO;
import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.service.BookService;


@ExtendWith(SpringExtension.class)
@ContextConfiguration
@ActiveProfiles("test")
@WebMvcTest(LoanController.class)
public class LoanControllerTest {

    
    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Deve realizar um emprestimo")
    public void shouldLoanBook() throws Exception {
        LoanDTO dto = LoanDTO.builder().isbn("123").custumer("custumer").build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getByIsbn(dto.getIsbn())).willReturn(
            Optional.of(Book.builder()
                            .id(1L)
                            .isbn("isbn")
                            .build()));

        var request = MockMvcRequestBuilders.post(json, null)
    }
}