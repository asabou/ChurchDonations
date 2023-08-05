package com.rurbisservices.churchdonation.service.helpers;

import com.rurbisservices.churchdonation.dao.entity.DonationEntity;
import com.rurbisservices.churchdonation.service.model.DonationDTO;
import com.rurbisservices.churchdonation.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.*;

public class DonationTransformer {
    public static void fillDonationDTO(final DonationEntity input, final DonationDTO target) {
        target.setId(input.getId());
        target.setReceipt(input.getReceipt().toString());
        target.setReceiptNew(input.getReceipt().toString());
        target.setSume(String.format("%.0f", input.getSume()));
        target.setDonationTopics(input.getDonationTopics());
        target.setDetails(input.getDetails());
        target.setCreationDate(input.getCreationDate());
        target.setUpdateDate(input.getUpdateDate());
        target.setDate(DateUtils.convertToddMMyyyy(input.getUpdateDate()));
        target.setChurchId(input.getChurchId());
        target.setPersonId(input.getPersonId());
        target.setHouseId(input.getHouseId());
    }

    public static void fillDonationEntity(final DonationDTO input, final DonationEntity target) {
        if (!isObjectNull(input.getId())) target.setId(input.getId());
        if (!isStringNullOrEmpty(input.getReceiptNew())) target.setReceipt(Long.parseLong(input.getReceiptNew()));
        if (!isStringNullOrEmpty(input.getSume())) target.setSume(Double.parseDouble(input.getSume()));
        if (!isStringNullOrEmpty(input.getDonationTopics())) target.setDonationTopics(input.getDonationTopics());
        if (!isStringNullOrEmpty(input.getDetails())) target.setDetails(returnTrimOrNull(input.getDetails()));
        if (!isObjectNull(input.getCreationDate())) target.setCreationDate(input.getCreationDate());
        if (!isObjectNull(input.getUpdateDate())) target.setUpdateDate(input.getUpdateDate());
        if (!isObjectNull(input.getChurchId())) target.setChurchId(input.getChurchId());
        if (!isObjectNull(input.getPersonId())) target.setPersonId(input.getPersonId());
        if (!isObjectNull(input.getHouseId())) target.setHouseId(input.getHouseId());
    }

    public static DonationDTO transformDonationEntity(final DonationEntity input) {
        final DonationDTO target = new DonationDTO();
        if (!isObjectNull(input)) {
            fillDonationDTO(input, target);
        }
        return target;
    }

    public static DonationEntity transformDonationDTO(final DonationDTO input) {
        final DonationEntity target = new DonationEntity();
        if (!isObjectNull(input)) {
            fillDonationEntity(input, target);
        }
        return target;
    }

    public static List<DonationDTO> transformDonationEntities(final List<DonationEntity> inputs) {
        List<DonationDTO> targets = new ArrayList<>();
        inputs.forEach(input -> targets.add(transformDonationEntity(input)));
        return targets;
    }
}
