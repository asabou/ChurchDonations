package com.rurbisservices.churchdonation.validators;

import com.rurbisservices.churchdonation.service.model.DonationDTO;
import org.springframework.stereotype.Component;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.*;

@Component
public class DonationValidation extends AbstractValidation<DonationDTO> {
    @Override
    public void validate(DonationDTO donationDTO) {
        if (isObjectNull(donationDTO) || isStringNullOrEmpty(donationDTO.getReceiptNew()) || isObjectNull(donationDTO.getSume())
                || isObjectNull(donationDTO.getHouseId()) || isObjectNull(donationDTO.getChurchId())) {
            throwInternalServerErrorException(402);
        } else {
            if (!isStringDouble(donationDTO.getSume()) || (isStringDouble(donationDTO.getSume()) && Double.parseDouble(donationDTO.getSume()) < 0)) {
                throwInternalServerErrorException(403);
            }
            if (!isStringLong(donationDTO.getReceiptNew()) || (isStringLong(donationDTO.getReceiptNew()) && Long.parseLong(donationDTO.getReceiptNew()) < 0)) {
                throwInternalServerErrorException(404);
            }
        }
    }
}
