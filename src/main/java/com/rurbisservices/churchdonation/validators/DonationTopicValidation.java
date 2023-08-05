package com.rurbisservices.churchdonation.validators;

import com.rurbisservices.churchdonation.service.model.DonationTopicDTO;
import org.springframework.stereotype.Component;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.isObjectNull;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.isStringNullOrEmpty;

@Component
public class DonationTopicValidation extends AbstractValidation<DonationTopicDTO> {
    @Override
    public void validate(DonationTopicDTO donationTopicDTO) {
        if (isObjectNull(donationTopicDTO) || isStringNullOrEmpty(donationTopicDTO.getTopic()) || isObjectNull(donationTopicDTO.getChurchId())) {
            throwInternalServerErrorException(503);
        }
    }
}
