package com.talles.transactionservice.external;

import lombok.Data;

@Data
public class ExchangeRateItem {

    private String record_date;
    private String country;
    private String currency;
    private String country_currency_desc;
    private Double exchange_rate;
    private String effective_date;

}
