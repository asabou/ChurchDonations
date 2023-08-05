package com.rurbisservices.churchdonation.utils.converters;

import com.rurbisservices.churchdonation.service.model.PersonDTO;
import javafx.util.StringConverter;

import static com.rurbisservices.churchdonation.utils.Constants.EMPTY_STRING;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.isObjectNull;

public class PersonDTOConverter extends StringConverter<PersonDTO> {
    @Override
    public String toString(PersonDTO personDTO) {
        if (!isObjectNull(personDTO)) {
            return personDTO.displayFormat();
        }
        return EMPTY_STRING;
    }

    @Override
    public PersonDTO fromString(String s) {
        return null;
    }
}
