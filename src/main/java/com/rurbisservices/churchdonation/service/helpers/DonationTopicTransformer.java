package com.rurbisservices.churchdonation.service.helpers;

import com.rurbisservices.churchdonation.dao.entity.DonationTopicEntity;
import com.rurbisservices.churchdonation.service.model.DonationTopicDTO;
import com.rurbisservices.churchdonation.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.*;

public class DonationTopicTransformer {
    public static void fillDonationTopicDTO(final DonationTopicEntity input, final DonationTopicDTO target) {
        target.setId(input.getId());
        target.setTopic(input.getTopic());
        target.setDetails(input.getDetails());
        target.setCreationDate(input.getCreationDate());
        target.setUpdateDate(input.getCreationDate());
        target.setDate(DateUtils.convertToddMMyyyy(input.getCreationDate()));
        target.setChurchId(input.getChurch().getId());
    }

    public static void fillDonationTopicEntity(final DonationTopicDTO input, final DonationTopicEntity target) {
        if (!isObjectNull(input.getId())) target.setId(input.getId());
        if (!isStringNullOrEmpty(input.getTopic())) target.setTopic(returnTrimOrNull(input.getTopic()));
        if (!isStringNullOrEmpty(input.getDetails())) target.setDetails(returnTrimOrNull(input.getDetails()));
        if (!isObjectNull(input.getCreationDate())) target.setCreationDate(input.getCreationDate());
        if (!isObjectNull(input.getUpdateDate())) target.setUpdateDate(input.getUpdateDate());
    }

    public static DonationTopicDTO transformDonationTopicEntity(final DonationTopicEntity input) {
        final DonationTopicDTO target = new DonationTopicDTO();
        if (!isObjectNull(input)) {
            fillDonationTopicDTO(input, target);
        }
        return target;
    }

    public static DonationTopicEntity transformDonationTopicDTO(final DonationTopicDTO input) {
        final DonationTopicEntity target = new DonationTopicEntity();
        if (!isObjectNull(input)) {
            fillDonationTopicEntity(input, target);
        }
        return target;
    }

    public static List<DonationTopicDTO> transformDonationTopicEntities(List<DonationTopicEntity> inputs) {
        List<DonationTopicDTO> targets = new ArrayList<>();
        inputs.forEach(input -> targets.add(transformDonationTopicEntity(input)));
        return targets;
    }
}
