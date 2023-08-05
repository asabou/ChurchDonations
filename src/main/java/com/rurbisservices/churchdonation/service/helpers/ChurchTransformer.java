package com.rurbisservices.churchdonation.service.helpers;

import com.rurbisservices.churchdonation.dao.entity.ChurchEntity;
import com.rurbisservices.churchdonation.service.model.ChurchDTO;

import java.util.ArrayList;
import java.util.List;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.*;

public class ChurchTransformer {
    public static void fillChurchDTO(final ChurchEntity input, final ChurchDTO target) {
        target.setId(input.getId());
        target.setName(input.getName());
        target.setNameNew(input.getName());
        target.setNumber(input.getNumber());
        target.setStreet(input.getStreet());
        target.setDetails(input.getDetails());
        target.setCreationDate(input.getCreationDate());
        target.setUpdateDate(input.getUpdateDate());
    }

    public static void fillChurchEntity(final ChurchDTO input, final ChurchEntity target) {
        if (!isObjectNull(input.getId())) target.setId(input.getId());
        if (!isStringNullOrEmpty(input.getNameNew())) target.setName(returnTrimOrNull(input.getNameNew()));
        if (!isStringNullOrEmpty(input.getNumber())) target.setNumber(returnTrimOrNull(input.getNumber()));
        if (!isStringNullOrEmpty(input.getStreet())) target.setStreet(returnTrimOrNull(input.getStreet()));
        if (!isStringNullOrEmpty(input.getDetails())) target.setDetails(returnTrimOrNull(input.getDetails()));
        if (!isObjectNull(input.getCreationDate())) target.setCreationDate(input.getCreationDate());
        if (!isObjectNull(input.getUpdateDate())) target.setUpdateDate(input.getUpdateDate());
    }

    public static ChurchDTO transformChurchEntity(final ChurchEntity input) {
        ChurchDTO target = new ChurchDTO();
        if (!isObjectNull(input)) {
            fillChurchDTO(input, target);
        }
        return target;
    }

    public static ChurchEntity transformChurchDTO(final ChurchDTO input) {
        ChurchEntity target = new ChurchEntity();
        if (!isObjectNull(input)) {
            fillChurchEntity(input, target);
        }
        return target;
    }

    public static List<ChurchDTO> transformChurchEntities(List<ChurchEntity> inputs) {
        List<ChurchDTO> targets = new ArrayList<>();
        inputs.forEach(input -> targets.add(transformChurchEntity(input)));
        return targets;
    }

    public static List<ChurchEntity> transformChurchDTOs(List<ChurchDTO> inputs) {
        List<ChurchEntity> targets = new ArrayList<>();
        inputs.forEach(input -> targets.add(transformChurchDTO(input)));
        return targets;
    }
}
