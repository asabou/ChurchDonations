package com.rurbisservices.churchdonation.utils.converters;

import com.rurbisservices.churchdonation.service.model.ChurchDTO;
import javafx.util.StringConverter;

import static com.rurbisservices.churchdonation.utils.Constants.EMPTY_STRING;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.isObjectNull;

public class ChurchDTOConverter extends StringConverter<ChurchDTO> {
    @Override
    public String toString(ChurchDTO o) {
        if (!isObjectNull(o)) {
            return o.getNameNew();
        }
        return EMPTY_STRING;
    }

    @Override
    public ChurchDTO fromString(String s) {
        return null;
    }
}
