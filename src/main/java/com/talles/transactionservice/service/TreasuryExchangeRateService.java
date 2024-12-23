package com.talles.transactionservice.service;

import com.talles.transactionservice.external.ExchangeRateResponse;
import com.talles.transactionservice.external.TreasuryExchangeRateClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class TreasuryExchangeRateService implements ExchangeRateService {

    private final TreasuryExchangeRateClient exchangeRateClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TreasuryExchangeRateService(TreasuryExchangeRateClient exchangeRateClient) {
        this.exchangeRateClient = exchangeRateClient;
    }

    /**
     * Get the most recent exchange rate in the interval between transactionDate and (transactionDate - 6 months).
     * Returns null if no exchange rate is found.
     */
    @Override
    public Double getExchangeRate(LocalDate transactionDate, String countryCurrency) {

        LocalDate minDate = transactionDate.minusMonths(6);

        StringBuilder filter = new StringBuilder();
        filter.append("record_date:lte:").append(transactionDate.format(formatter))
                .append(",record_date:gte:").append(minDate.format(formatter))
                .append(",country_currency_desc:eq:").append(countryCurrency);

        log.info("getExchangeRate(): filter = {}", filter);

        ExchangeRateResponse exchangeRateResponse = exchangeRateClient.getExchangeRate(filter.toString(), "record_date", 1);

        if (exchangeRateResponse == null || exchangeRateResponse.getData() == null ||
            exchangeRateResponse.getData().isEmpty()) {
            return null;
        }

        return exchangeRateResponse.getData().getFirst().getExchange_rate();
    }
}
