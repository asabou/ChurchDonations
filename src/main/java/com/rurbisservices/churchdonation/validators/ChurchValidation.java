package com.rurbisservices.churchdonation.validators;

import com.rurbisservices.churchdonation.service.model.ChurchDTO;
import org.springframework.stereotype.Component;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.isObjectNull;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.isStringNullOrEmpty;

@Component
public class ChurchValidation extends AbstractValidation<ChurchDTO> {
    @Override
    public void validate(ChurchDTO church) {
        if (isObjectNull(church) || isStringNullOrEmpty(church.getNameNew())) {
            throwInternalServerErrorException(103);
        }
    }
}
