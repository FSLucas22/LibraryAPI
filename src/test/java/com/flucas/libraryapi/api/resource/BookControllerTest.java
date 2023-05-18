package com.flucas.libraryapi.api.resource;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flucas.libraryapi.api.dto.BookDTO;
import com.flucas.libraryapi.exceptions.BusinessException;
import com.flucas.libraryapi.model.entity.Book;
import com.flucas.libraryapi.service.BookService;


@ExtendWith(SpringExtension.class)
@ContextConfiguration
@ActiveProfiles("test")
@WebMvcTest
public class BookControllerTest {
    static String BOOK_API = "/api/books";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService service;

    private BookDTO dto;
    private Book book;

    @BeforeEach
    public void setUp() {
        dto = BookDTO.builder()
                .title("Livro Teste x")
                .author("Autor Teste x")
                .isbn("123123 x").build();

        book = Book.builder()
                .id(10L)
                .title("Livro Teste x")
                .author("Autor Teste x")
                .isbn("123123 x")
                .build();
    }

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    void createBookTest() throws Exception {

        BDDMockito
                .given(service.save(Mockito.any(Book.class)))
                .willReturn(book);

        String json = new ObjectMapper().writeValueAsString(dto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(book.getId()))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes")
    void shouldThrowError() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookDTO());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Não deve cadastrar ISBN repetido")
    public void shouldNotRepeatIsbn() throws Exception {
        String json = new ObjectMapper().writeValueAsString(dto);
        String msg = "ISBN já cadastrado.";
        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(
                new BusinessException(msg)
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);
        
        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("errors", 
                        Matchers.hasSize(1)))
                .andExpect(
                        jsonPath("errors[0]")
                        .value(msg));
    }

    @Test
    @DisplayName("Deve retornar os detalhes de um livro")
    public void shouldReturnBookDetails() throws Exception {
        Long id = 10L;
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        var request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);
        
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro não existe")
    public void shouldReturnResourceNotFound() throws Exception {
        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        var request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 10))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void shouldDeleteBook() throws Exception {
        Long id = 1L;
        ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);

        var request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNoContent());
        
        verify(service).delete(captor.capture());
        assertEquals(id, captor.getValue().getId());
    }
}
