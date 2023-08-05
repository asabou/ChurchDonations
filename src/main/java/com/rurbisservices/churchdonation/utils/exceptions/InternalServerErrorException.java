package com.rurbisservices.churchdonation.utils.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
        log.error(message);
    }
}
