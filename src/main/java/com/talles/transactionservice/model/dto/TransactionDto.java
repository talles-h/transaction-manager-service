package com.talles.transactionservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

/**
 * Data Transfer Object used when returning a stored transaction.
 */
@Data
@JsonInclude(Include.NON_NULL)
public class TransactionDto {

    @Schema(description = "The transaction unique identifier")
    private Long id;

    @Schema(description = "Description of the transaction.", maximum = "50")
    private String description;

    @Schema(description = "The transaction date and time in ISO format")
    private LocalDateTime transactionDate;

    @Schema(description = "The transaction amount in US Dollars")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#.00")
    private BigDecimal amountUSD;

    @Schema(description = "Details about the required amount conversion to a target currency")
    private ConversionDetails conversionDetails;
}
