package com.flucas.libraryapi.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Loan {
    private Long id;
    private Book book;
    private String customer;
    private Boolean returned;
}
