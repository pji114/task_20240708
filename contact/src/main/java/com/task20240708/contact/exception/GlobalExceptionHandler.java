package com.task20240708.contact.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.task20240708.contact.exception.ExceptionObj.ErrorResponse;

/**
 * Exception Handler
 * 예외 상황에 대한 별도의 처리가 필요할 경우 사용한다
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler{
    
    /**
     * 일반적인 에러가 발생했을때 사용
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleException(Exception ex){
        String errorMsg = "INTERNAL_SERVER_ERROR!";
        ex.printStackTrace();
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, errorMsg);

    }

    /**
     * 파라미터가 잘못 됐을 때
     * @param ex
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex){
        String errorMsg = "BAD_REQUEST!";
        ex.printStackTrace();
        return buildResponseEntity(HttpStatus.BAD_REQUEST, errorMsg);

    }

    /**
     * 에러 Response 시 사용할 객체를 정의
     * @param status http status code
     * @param message 응답할 메세지
     * @return
     */
    private ResponseEntity<Object> buildResponseEntity(HttpStatus status, String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
        .status(status.value())
        .message(message)
        .build();
        return new ResponseEntity<>(errorResponse, status);
    }
}
