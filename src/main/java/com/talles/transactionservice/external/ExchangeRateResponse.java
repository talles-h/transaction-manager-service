package com.talles.transactionservice.external;

import lombok.Data;

import java.util.List;

@Data
public class ExchangeRateResponse {
    private List<ExchangeRateItem> data;
}
