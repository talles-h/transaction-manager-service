package com.talles.transactionservice.service;

import com.talles.transactionservice.model.dto.TransactionCreationDto;
import com.talles.transactionservice.model.dto.TransactionDto;

import java.util.List;

public interface TransactionService {

    TransactionDto createTransaction(TransactionCreationDto transaction);

    TransactionDto getTransactionById(Long id, String countryCurrency);

    List<TransactionDto> getTransactions(String countryCurrency, Integer page, Integer size, String sortBy, boolean asc);

}
