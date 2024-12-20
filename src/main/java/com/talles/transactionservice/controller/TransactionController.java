package com.talles.transactionservice.controller;


import com.talles.transactionservice.model.dto.TransactionCreationDto;
import com.talles.transactionservice.model.dto.TransactionDto;
import com.talles.transactionservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public  TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Store a new transaction", description = "Store the new transaction and returns the new generated resource.")
    @PostMapping
    public ResponseEntity<TransactionDto> storeTransaction(@RequestBody @Valid TransactionCreationDto transactionBody) {

        TransactionDto transactionDto = transactionService.createTransaction(transactionBody);

        // Build the URI for the location header.
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(transactionDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(transactionDto);
    }

    @Operation(summary = "Retrieve a transaction",
            description = "Retrieve a transaction by ID. The transaction amount can be converted and returned in the" +
                    " specified countryCurrency query parameter")
    @Parameter(name = "id", description = "The transaction unique identifier", required = true)
    @Parameter(name = "countryCurrency", description = "The country and currency required to be returned. Format must be country-currency.",
            example = "Argentina-Peso")
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable Long id, @RequestParam(required = false) String countryCurrency) {
        TransactionDto transactionDto = transactionService.getTransaction(id, countryCurrency);

        return ResponseEntity.ok(transactionDto);
    }
}
