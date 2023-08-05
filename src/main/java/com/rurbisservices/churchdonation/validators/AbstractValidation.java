package com.rurbisservices.churchdonation.validators;

import com.rurbisservices.churchdonation.utils.MessagesUtils;
import com.rurbisservices.churchdonation.utils.exceptions.BadRequestException;
import com.rurbisservices.churchdonation.utils.exceptions.InternalServerErrorException;
import com.rurbisservices.churchdonation.utils.exceptions.NotFoundException;

public abstract class AbstractValidation<T> {
    public void throwNotFoundException(Integer code, Object ...params) {
        String message = MessagesUtils.getMessage(code);
        throw new NotFoundException(String.format(message, params));
    }

    public void throwBadRequestException(Integer code, Object ...params) {
        String message = MessagesUtils.getMessage(code);
        throw new BadRequestException(String.format(message, params));
    }

    public void throwInternalServerErrorException(Integer code, Object ...params) {
        String message = MessagesUtils.getMessage(code);
        throw new InternalServerErrorException(String.format(message, params));
    }

    public abstract void validate(T t);
}
