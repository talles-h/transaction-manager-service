package com.talles.transactionservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConversionDetails {

    @Schema(description = "The transaction amount converted to a target country currency")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#.00")
    private BigDecimal amount;

    @Schema(description = "The country and currency of this amount", example = "Argentina-Peso")
    private String countryCurrency;

    @Schema(description = "The exchange rate used in the currency conversion")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#.00")
    private BigDecimal exchangeRate;
}
