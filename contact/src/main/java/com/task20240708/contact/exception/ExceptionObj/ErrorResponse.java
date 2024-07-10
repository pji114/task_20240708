package com.task20240708.contact.exception.ExceptionObj;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class ErrorResponse {
    private int status;
    private String message;   
}
