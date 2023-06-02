package com.flucas.libraryapi.api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private Long   id;

    @NotEmpty
    private String isbn;

    @NotEmpty
    private String customer;

    private BookDTO book;
}
