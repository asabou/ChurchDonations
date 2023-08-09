package com.rurbisservices.churchdonation.validators;

import com.rurbisservices.churchdonation.service.model.DonationDTO;
import com.rurbisservices.churchdonation.service.model.SumeDonationTopicDTO;
import org.springframework.stereotype.Component;

import java.util.List;

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
            Double total = 0.0;
            List<SumeDonationTopicDTO> sumeDonationTopicDTOList = donationDTO.getSumeDonationTopics();
            for (SumeDonationTopicDTO sume : sumeDonationTopicDTOList) {
                if (isStringNullOrEmpty(sume.getTopic()) || isStringNullOrEmpty(sume.getSume()) || !isStringDouble(sume.getSume())) {
                    throwInternalServerErrorException(406);
                } else {
                    total = total + Double.parseDouble(sume.getSume());
                }
            }
            if (total != Double.parseDouble(donationDTO.getSume())) {
                throwInternalServerErrorException(407, String.format("%.0f", total), donationDTO.getSume());
            }
        }
    }
}
