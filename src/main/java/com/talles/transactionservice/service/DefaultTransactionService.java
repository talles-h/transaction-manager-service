package com.talles.transactionservice.service;

import com.talles.transactionservice.exception.EntityNotFoundException;
import com.talles.transactionservice.exception.ExchangeRateNotFoundException;
import com.talles.transactionservice.model.dto.ConversionDetails;
import com.talles.transactionservice.model.dto.TransactionCreationDto;
import com.talles.transactionservice.model.dto.TransactionDto;
import com.talles.transactionservice.model.entity.Transaction;
import com.talles.transactionservice.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultTransactionService implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ExchangeRateService exchangeRateService;

    public DefaultTransactionService(TransactionRepository transactionRepository, ExchangeRateService exchangeRateService) {
        this.transactionRepository = transactionRepository;
        this.exchangeRateService = exchangeRateService;
    }

    @Override
    public TransactionDto createTransaction(TransactionCreationDto transactionCreationDto) {

        if (transactionCreationDto.getTransactionDate() == null) {
            transactionCreationDto.setTransactionDate(ZonedDateTime.now(ZoneOffset.UTC));
        }

        Transaction transaction = new Transaction();
        transaction.setDescription(transactionCreationDto.getDescription());
        transaction.setAmountUSD(transactionCreationDto.getAmountUSD());
        transaction.setTransactionDate(transactionCreationDto.getTransactionDate());

        transaction = transactionRepository.save(transaction);

        return createTransactionDto(transaction);
    }

    @Override
    public TransactionDto getTransactionById(Long id, String countryCurrency) {

        Optional<Transaction> transactionOp = transactionRepository.findById(id);
        if (transactionOp.isEmpty()) {
            throw new EntityNotFoundException("Transaction not found");
        }

        TransactionDto transactionDto = createTransactionDto(transactionOp.get());
        addConversionDetails(transactionDto, countryCurrency);

        return transactionDto;
    }

    @Override
    public List<TransactionDto> getTransactions(String countryCurrency, Integer page, Integer size, String sortBy, boolean asc) {


        List<Transaction> transactions;
        Sort sort = asc ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        if (page != null || size != null) {
            Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : 10, sort);
            Page<Transaction> pagedTransactions =  transactionRepository.findAll(pageable);
            transactions = pagedTransactions.getContent();
        } else {
            transactions = transactionRepository.findAll(sort);
        }

        List<TransactionDto> transactionDtoList = new ArrayList<>(transactions.size());

        for (Transaction transaction : transactions) {
            TransactionDto transactionDto = createTransactionDto(transaction);
            addConversionDetails(transactionDto, countryCurrency);
            transactionDtoList.add(transactionDto);
        }

        return transactionDtoList;
    }

    private void addConversionDetails(TransactionDto transactionDto, String countryCurrency) {
        if (StringUtils.hasLength(countryCurrency)) {
            Double exchangeRate = exchangeRateService.getExchangeRate(transactionDto.getTransactionDate().toLocalDate(),
                    countryCurrency);

            if (exchangeRate == null) {
                throw new ExchangeRateNotFoundException();
            }

            BigDecimal exRate = BigDecimal.valueOf(exchangeRate);
            BigDecimal roundedResult = transactionDto.getAmountUSD().multiply(exRate).setScale(2, RoundingMode.HALF_DOWN);

            transactionDto.setConversionDetails(new ConversionDetails(roundedResult, countryCurrency, exRate));
        }
    }

    private TransactionDto createTransactionDto(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setId(transaction.getId());
        transactionDto.setTransactionDate(transaction.getTransactionDate());
        transactionDto.setDescription(transaction.getDescription());
        transactionDto.setAmountUSD(transaction.getAmountUSD());

        return transactionDto;
    }

}
