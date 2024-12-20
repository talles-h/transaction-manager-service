package com.talles.transactionservice.exception;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StandardErrorResponse {

    private HttpStatus status;
    private String message;
    private Map<String, String> details;
}
