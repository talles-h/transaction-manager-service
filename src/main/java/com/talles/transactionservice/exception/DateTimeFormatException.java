package com.talles.transactionservice.exception;

import lombok.Getter;

@Getter
public class DateTimeFormatException extends RuntimeException {

    private final String field;

    public DateTimeFormatException(String field, String message) {
        super(message);
        this.field = field;
    }
}
