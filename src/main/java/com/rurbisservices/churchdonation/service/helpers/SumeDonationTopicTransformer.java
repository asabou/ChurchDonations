package com.rurbisservices.churchdonation.service.helpers;

import com.rurbisservices.churchdonation.dao.entity.SumeDonationTopicEntity;
import com.rurbisservices.churchdonation.service.model.SumeDonationTopicDTO;

import java.util.ArrayList;
import java.util.List;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.*;

public class SumeDonationTopicTransformer {
    public static void fillSumeDonationTopicDTO(final SumeDonationTopicEntity input, final SumeDonationTopicDTO target) {
        target.setId(input.getId());
        target.setDonationId(input.getDonation().getId());
        target.setTopic(input.getTopic());
        target.setSume(String.format("%.0f", input.getSume()));
    }

    public static void fillSumeDonationTopicEntity(final SumeDonationTopicDTO input, final SumeDonationTopicEntity target) {
        if (!isObjectNull(input.getId())) target.setId(input.getId());
        if (!isStringNullOrEmpty(input.getTopic())) target.setTopic(returnTrimOrNull(input.getTopic()));
        if (!isStringNullOrEmpty(input.getSume())) target.setSume(Double.parseDouble(input.getSume()));
    }

    public static SumeDonationTopicDTO transformSumeDonationTopicEntity(final SumeDonationTopicEntity input) {
        SumeDonationTopicDTO target = new SumeDonationTopicDTO();
        if (!isObjectNull(input)) {
            fillSumeDonationTopicDTO(input, target);
        }
        return target;
    }

    public static SumeDonationTopicEntity transformSumeDonationTopicDTO(SumeDonationTopicDTO input) {
        SumeDonationTopicEntity target = new SumeDonationTopicEntity();
        if (!isObjectNull(input)) {
            fillSumeDonationTopicEntity(input, target);
        }
        return target;
    }

    public static List<SumeDonationTopicDTO> transformSumeDonationTopicEntities(List<SumeDonationTopicEntity> inputs) {
        List<SumeDonationTopicDTO> targets = new ArrayList<>();
        inputs.forEach(input -> targets.add(transformSumeDonationTopicEntity(input)));
        return targets;
    }

    public static List<SumeDonationTopicEntity> transformSumeDonationTopicDTOS(List<SumeDonationTopicDTO> inputs) {
        List<SumeDonationTopicEntity> targets = new ArrayList<>();
        inputs.forEach(input -> targets.add(transformSumeDonationTopicDTO(input)));
        return targets;
    }
}
