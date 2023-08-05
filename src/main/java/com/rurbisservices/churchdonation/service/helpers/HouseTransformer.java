package com.rurbisservices.churchdonation.service.helpers;

import com.rurbisservices.churchdonation.dao.entity.HouseEntity;
import com.rurbisservices.churchdonation.service.model.HouseDTO;

import java.util.ArrayList;
import java.util.List;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.*;

public class HouseTransformer {
    public static void fillHouseEntity(final HouseDTO input, final HouseEntity target) {
        if (!isObjectNull(input.getId())) target.setId(input.getId());
        if (!isStringNullOrEmpty(input.getNumberNew())) target.setNumber(returnTrimOrNull(input.getNumberNew()));
        if (!isStringNullOrEmpty(input.getDetails())) target.setDetails(returnTrimOrNull(input.getDetails()));
        if (!isObjectNull(input.getCreationDate())) target.setCreationDate(input.getCreationDate());
        if (!isObjectNull(input.getUpdateDate())) target.setUpdateDate(input.getUpdateDate());
    }

    public static void fillHouseDTO(final HouseEntity input, final HouseDTO target) {
        target.setId(input.getId());
        target.setNumber(input.getNumber());
        target.setNumberNew(input.getNumber());
        target.setDetails(input.getDetails());
        target.setCreationDate(input.getCreationDate());
        target.setUpdateDate(input.getUpdateDate());
        target.setChurchId(input.getChurch().getId());
    }

    public static HouseEntity transformHouseDTO(final HouseDTO input) {
        final HouseEntity target = new HouseEntity();
        if (!isObjectNull(input)) {
            fillHouseEntity(input, target);
        }
        return target;
    }

    public static HouseDTO transformHouseEntity(final HouseEntity input) {
        final HouseDTO target = new HouseDTO();
        if (!isObjectNull(input)) {
            fillHouseDTO(input, target);
        }
        return target;
    }

    public static List<HouseDTO> transformHouseEntities(final List<HouseEntity> inputs) {
        List<HouseDTO> targets = new ArrayList<>();
        inputs.forEach(input -> targets.add(transformHouseEntity(input)));
        return targets;
    }
}
