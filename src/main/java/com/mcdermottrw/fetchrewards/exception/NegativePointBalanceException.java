package com.mcdermottrw.fetchrewards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class NegativePointBalanceException extends RuntimeException {

    public NegativePointBalanceException(String message) {
        super(message);
    }

}
