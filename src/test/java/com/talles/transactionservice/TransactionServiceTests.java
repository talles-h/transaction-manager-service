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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

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

    @Test
    void getTransactionByIdTest_Success() {
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmountUSD(new BigDecimal("10.31"));
        transaction.setDescription("Transaction Test Get 1");

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
        when(exchangeRateService.getExchangeRate(any(), eq("Brazil-Real"))).thenReturn(2.0);

        TransactionDto transactionDto = transactionService.getTransaction(1L, "Brazil-Real");
        assertNotNull(transactionDto);
        assertEquals(transaction.getId(), transactionDto.getId());
        assertEquals(transaction.getAmountUSD(), transactionDto.getAmountUSD());

        ConversionDetails conversionDetails = transactionDto.getConversionDetails();
        assertNotNull(conversionDetails);
        assertEquals(transaction.getAmountUSD().multiply(BigDecimal.valueOf(2)), conversionDetails.getAmount());
    }
}
