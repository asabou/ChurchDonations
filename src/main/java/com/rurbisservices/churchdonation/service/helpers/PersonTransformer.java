package com.rurbisservices.churchdonation.service.helpers;

import com.rurbisservices.churchdonation.dao.entity.PersonEntity;
import com.rurbisservices.churchdonation.service.model.PersonDTO;
import com.rurbisservices.churchdonation.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.*;

public class PersonTransformer {
    public static void fillPersonDTO(PersonEntity input, PersonDTO target) {
        target.setId(input.getId());
        target.setFirstName(input.getFirstName());
        target.setLastName(input.getLastName());
        target.setDetails(input.getDetails());
        target.setCreationDate(input.getCreationDate());
        target.setUpdateDate(input.getUpdateDate());
        target.setHouseId(input.getHouse().getId());
        target.setHouse(input.getHouse().getNumber());
    }

    public static void fillPersonEntity(PersonDTO input, PersonEntity target) {
        if (!isObjectNull(input.getId())) target.setId(input.getId());
        if (!isStringNullOrEmpty(input.getFirstName())) target.setFirstName(returnTrimOrNull(input.getFirstName()));
        if (!isStringNullOrEmpty(input.getLastName())) target.setLastName(returnTrimOrNull(input.getLastName()));
        if (!isStringNullOrEmpty(input.getDetails())) target.setDetails(returnTrimOrNull(input.getDetails()));
        if (!isObjectNull(input.getCreationDate())) target.setCreationDate(input.getCreationDate());
        if (!isObjectNull(input.getUpdateDate())) target.setUpdateDate(input.getUpdateDate());
    }

    public static PersonDTO transformPersonEntity(PersonEntity input) {
        PersonDTO target = new PersonDTO();
        if (!isObjectNull(input)) {
            fillPersonDTO(input, target);
        }
        return target;
    }

    public static PersonEntity transformPersonDTO(PersonDTO input) {
        PersonEntity target = new PersonEntity();
        if (!isObjectNull(input)) {
            fillPersonEntity(input, target);
        }
        return target;
    }

    public static List<PersonDTO> transformPersonEntities(List<PersonEntity> inputs) {
        List<PersonDTO> targets = new ArrayList<>();
        inputs.forEach(input -> targets.add(transformPersonEntity(input)));
        return targets;
    }

}
