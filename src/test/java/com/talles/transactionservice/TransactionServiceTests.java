package com.talles.transactionservice;

import com.talles.transactionservice.model.dto.ConversionDetails;
import com.talles.transactionservice.model.dto.TransactionDto;
import com.talles.transactionservice.model.entity.Transaction;
import com.talles.transactionservice.repository.TransactionRepository;
import com.talles.transactionservice.service.DefaultTransactionService;
import com.talles.transactionservice.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

/**
 * Basic Unit tests for DefaultTransactionService.
 * This is a minimum implementation just to show off how a specific service can be tested.
 */
@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@EnabledIf(expression = "#{environment.acceptsProfiles('unit-tests')}", loadContext = true)
class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ExchangeRateService exchangeRateService;

    @InjectMocks
    private DefaultTransactionService transactionService;

    /**
     * Make sure the service can return a transaction with a currency conversion.
     */
    @Test
    void getTransactionByIdTest_Success() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmountUSD(new BigDecimal("10.31"));
        transaction.setDescription("Transaction Test Get 1");

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(exchangeRateService.getExchangeRate(any(), eq("Brazil-Real"))).thenReturn(2.0);

        TransactionDto transactionDto = transactionService.getTransactionById(1L, "Brazil-Real");
        assertNotNull(transactionDto);
        assertEquals(transaction.getId(), transactionDto.getId());
        assertEquals(transaction.getAmountUSD(), transactionDto.getAmountUSD());

        ConversionDetails conversionDetails = transactionDto.getConversionDetails();
        assertNotNull(conversionDetails);
        assertEquals(transaction.getAmountUSD().multiply(BigDecimal.valueOf(2)), conversionDetails.getAmount());
    }

    /**
     * Make sure not errors happen when we don't have any transaction stored.
     */
    @Test
    void getTransactionsEmpty_Success() {
        when(transactionRepository.findAll(Mockito.any(Sort.class))).thenReturn(Collections.emptyList());

        List<TransactionDto> transactionDtoList = transactionService.getTransactions("Brazil-Real",
                null, null, "id", true);
        assertNotNull(transactionDtoList);
        assertEquals(0, transactionDtoList.size());
    }

    /**
     * Make sure /transactions can return a list with at least one transaction.
     */
    @Test
    void getTransactions_Success() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmountUSD(new BigDecimal("10.31"));
        transaction.setDescription("Transaction Test Get 1");

        when(transactionRepository.findAll(Mockito.any(Sort.class))).thenReturn(List.of(transaction));
        when(exchangeRateService.getExchangeRate(any(), eq("Brazil-Real"))).thenReturn(2.0);

        List<TransactionDto> transactionDtoList = transactionService.getTransactions("Brazil-Real",
                null, null, "id", true);
        assertNotNull(transactionDtoList);
        assertEquals(1, transactionDtoList.size());
        assertEquals(transaction.getId(), transactionDtoList.getFirst().getId());
        assertEquals(transaction.getAmountUSD(), transactionDtoList.getFirst().getAmountUSD());

        ConversionDetails conversionDetails = transactionDtoList.getFirst().getConversionDetails();
        assertNotNull(conversionDetails);
        assertEquals(transaction.getAmountUSD().multiply(BigDecimal.valueOf(2)), conversionDetails.getAmount());
    }
}
