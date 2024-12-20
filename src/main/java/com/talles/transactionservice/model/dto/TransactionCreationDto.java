package com.talles.transactionservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object used when creating (storing) a new transaction.
 */
@Data
public class TransactionCreationDto {

    @NotBlank
    @Size(max = 50, message = "Must not exceed 50 characters")
    @Schema(description = "Description of the transaction.", maximum = "50")
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Schema(description = "The transaction date and time in ISO format")
    private LocalDateTime transactionDate;

    @NotNull
    @DecimalMin(value = "0.01", message = "Must be a valid positive amount rounded to the nearest cent")
    @Digits(integer = Integer.MAX_VALUE, fraction = 2, message = "Amount must have up to 2 decimal places")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#.00")
    @Schema(description = "The transaction amount in US Dollars")
    private BigDecimal amountUSD;
}
