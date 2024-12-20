package com.talles.transactionservice.service;

import java.time.LocalDate;

public interface ExchangeRateService {

    Double getExchangeRate(LocalDate transactionDate, String countryCurrency);

}
