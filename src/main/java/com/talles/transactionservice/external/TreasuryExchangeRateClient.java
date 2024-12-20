package com.talles.transactionservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "treasury-gov-api", url = "https://api.fiscaldata.treasury.gov")
public interface TreasuryExchangeRateClient {

    @GetMapping("services/api/fiscal_service/v1/accounting/od/rates_of_exchange")
    ExchangeRateResponse getExchangeRate(
            @RequestParam("filter") String filter,
            @RequestParam("sort") String sort,
            @RequestParam("limit") int limit
    );

}

@Component
class ExchangeRateFallbackClient implements  TreasuryExchangeRateClient {

    /**
     * Simple fallback client to return null in case of any error.
     * Ideally, we should treat the errors like 404, 403, 500, etc., to return a meaningful message.
     * Also, we should consider to do a retry in case of connection problem (timeout).
     */
    @Override
    public ExchangeRateResponse getExchangeRate(String filter, String sort, int limit) {
        return null;
    }
}
