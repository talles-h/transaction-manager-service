package com.talles.transactionservice.service;

import com.talles.transactionservice.model.dto.TransactionCreationDto;
import com.talles.transactionservice.model.dto.TransactionDto;

public interface TransactionService {

    TransactionDto createTransaction(TransactionCreationDto transaction);

    TransactionDto getTransaction(Long id, String countryCurrency);

}
