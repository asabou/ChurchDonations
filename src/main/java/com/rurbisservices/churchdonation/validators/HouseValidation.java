package com.rurbisservices.churchdonation.validators;

import com.rurbisservices.churchdonation.service.model.HouseDTO;
import org.springframework.stereotype.Component;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.isObjectNull;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.isStringNullOrEmpty;

@Component
public class HouseValidation extends AbstractValidation<HouseDTO> {
    @Override
    public void validate(HouseDTO houseDTO) {
        if (isObjectNull(houseDTO) || isStringNullOrEmpty(houseDTO.getNumberNew()) || isObjectNull(houseDTO.getChurchId())) {
            throwInternalServerErrorException(202);
        }
    }
}
