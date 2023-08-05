package com.rurbisservices.churchdonation.validators;

import com.rurbisservices.churchdonation.service.model.PersonDTO;
import org.springframework.stereotype.Component;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.isObjectNull;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.isStringNullOrEmpty;

@Component
public class PersonValidation extends AbstractValidation<PersonDTO> {
    @Override
    public void validate(PersonDTO personDTO) {
        if (isObjectNull(personDTO) || isStringNullOrEmpty(personDTO.getFirstName()) || isStringNullOrEmpty(personDTO.getLastName()) || isObjectNull(personDTO.getHouseId())) {
            throwInternalServerErrorException(302);
        }
    }
}
