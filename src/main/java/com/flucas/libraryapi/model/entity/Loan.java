package com.flucas.libraryapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Loan {
    private Long id;
    private String isbn;
    private String customer;
}
