package com.rurbisservices.churchdonation.utils.converters;

import com.rurbisservices.churchdonation.service.model.HouseDTO;
import com.rurbisservices.churchdonation.utils.Constants;
import javafx.util.StringConverter;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.isObjectNull;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.isStringNullOrEmpty;

public class HouseDTOConverter extends StringConverter<HouseDTO> {
    @Override
    public String toString(HouseDTO houseDTO) {
        return houseDTO.displayFormat();
    }

    @Override
    public HouseDTO fromString(String s) {
        return null;
    }
}
