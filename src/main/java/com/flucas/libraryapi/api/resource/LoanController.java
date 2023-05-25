package com.flucas.libraryapi.api.resource;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.flucas.libraryapi.api.dto.LoanDTO;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("api/loans")
@RequiredArgsConstructor
public class LoanController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanDTO create(@RequestBody LoanDTO dto) {
        return dto;
    }
}
