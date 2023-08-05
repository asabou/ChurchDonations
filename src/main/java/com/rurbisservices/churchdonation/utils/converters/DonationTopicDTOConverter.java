package com.rurbisservices.churchdonation.utils.converters;

import com.rurbisservices.churchdonation.service.model.DonationTopicDTO;
import javafx.util.StringConverter;

public class DonationTopicDTOConverter extends StringConverter<DonationTopicDTO> {
    @Override
    public String toString(DonationTopicDTO donationTopicDTO) {
        return donationTopicDTO.getTopic() + " " + donationTopicDTO.getDetails();
    }

    @Override
    public DonationTopicDTO fromString(String s) {
        return null;
    }
}
