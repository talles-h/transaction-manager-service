package com.talles.transactionservice.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.talles.transactionservice.utils.date.ZonedDateTimeDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Data Transfer Object used when creating (storing) a new transaction.
 */
@Data
public class TransactionCreationDto {

    @NotBlank
    @Size(max = 50, message = "Must not exceed 50 characters")
    @Schema(description = "Description of the transaction.", maximum = "50")
    private String description;

    @Schema(description = "The transaction UTC date and time in ISO format (yyyy-MM-dd'T'HH:mm:ss[.SSS]Z)." +
            "The time must be specified at least until the minutes. Can optionally have milliseconds." +
            "TIMEZONE: Must always be UTC. If the UTC timezone indicator (Z) is not present at end of the timezone, it will" +
            "be considered as UTC.",
            example = "2024-12-23T15:13:18.091Z")
    @JsonDeserialize(using = ZonedDateTimeDeserializer.class)
    private ZonedDateTime transactionDate;

    @NotNull
    @DecimalMin(value = "0.01", message = "Must be a valid positive amount rounded to the nearest cent")
    @Digits(integer = Integer.MAX_VALUE, fraction = 2, message = "Amount must have up to 2 decimal places")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#.00")
    @Schema(description = "The transaction amount in US Dollars")
    private BigDecimal amountUSD;
}
