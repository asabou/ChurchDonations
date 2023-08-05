package com.rurbisservices.churchdonation.service.impl;

import com.rurbisservices.churchdonation.dao.entity.HouseEntity;
import com.rurbisservices.churchdonation.dao.entity.PersonEntity;
import com.rurbisservices.churchdonation.service.abstracts.AbstractService;
import com.rurbisservices.churchdonation.service.abstracts.IPersonService;
import com.rurbisservices.churchdonation.service.helpers.HouseTransformer;
import com.rurbisservices.churchdonation.service.helpers.PersonTransformer;
import com.rurbisservices.churchdonation.service.model.HouseDTO;
import com.rurbisservices.churchdonation.service.model.PersonDTO;
import com.rurbisservices.churchdonation.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rurbisservices.churchdonation.utils.DateUtils.getCurrentTimestamp;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.isObjectNull;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.wildCardParam;

@Slf4j
@Service
public class PersonServiceImpl extends AbstractService implements IPersonService {
    @Override
    public List<PersonDTO> findAllByHouseId(Long houseId) {
        log.info("Trying to find people by house {}", houseId);
        List<PersonEntity> personEntities = personRepository.findAllByHouse_IdOrderByFirstName(houseId);
        log.info("Persons found: {}", personEntities.size());
        return PersonTransformer.transformPersonEntities(personEntities);
    }

    @Override
    public List<PersonDTO> findAllByChurchId(Long churchId) {
        log.info("Trying to find all persons from church {}", churchId);
        List<PersonEntity> personEntities = personRepository.findAllByChurchIdOrderByFirstNameAscLastNameAsc(churchId);
        log.info("Persons found {}", personEntities.size());
        return PersonTransformer.transformPersonEntities(personEntities);
    }

    @Override
    public List<PersonDTO> findAllByChurchIdAndFilter(Long churchId, String filter) {
        String filterWildCard = ServiceUtils.wildCardParam(filter);
        log.info("Trying to find all persons from church {}, and filter {}", churchId, filterWildCard);
        List<PersonEntity> personEntities = personRepository.findAllByChurchIdAndFilterOrderByFirstNameAscLastNameAsc(churchId, filterWildCard);
        log.info("Persons found {}", personEntities.size());
        return PersonTransformer.transformPersonEntities(personEntities);
    }

    @Override
    public List<PersonDTO> findAllByChurchIdAndHouseIdAndFilter(Long churchId, Long houseId, String filter) {
        String filterWildCard = wildCardParam(filter);
        log.info("Trying to find people by church {}, house {}, filter {}", churchId, houseId, filterWildCard);
        List<PersonEntity> personEntities = personRepository.findAllByHouseIdAndChurchIdAndFilterOrderByFirstNameAscLastNameAsc(houseId, churchId, filterWildCard);
        List<PersonDTO> personDTOS = PersonTransformer.transformPersonEntities(personEntities);
        log.info("Persons found {}", personDTOS.size());
        return personDTOS;
    }

    @Override
    public PersonDTO create(PersonDTO personDTO) {
        personDTO.setUpdateDate(getCurrentTimestamp());
        personDTO.setCreationDate(getCurrentTimestamp());
        log.info("Trying to create new person {}", personDTO);
        personValidation.validate(personDTO);
        HouseEntity houseEntity = findHouseEntityById(personDTO.getHouseId());
        PersonEntity personEntity = PersonTransformer.transformPersonDTO(personDTO);
        personEntity.setHouse(houseEntity);
        PersonEntity personEntitySaved = personRepository.save(personEntity);
        log.info("Person created");
        return PersonTransformer.transformPersonEntity(personEntitySaved);
    }

    @Override
    public PersonDTO update(PersonDTO personDTO) {
        personDTO.setUpdateDate(getCurrentTimestamp());
        log.info("Trying to update person {}", personDTO);
        personValidation.validate(personDTO);
        PersonEntity personEntity = findPersonEntityById(personDTO.getId());
        PersonTransformer.fillPersonEntity(personDTO, personEntity);
        PersonEntity personEntitySaved = personRepository.save(personEntity);
        log.info("Person updated ...");
        return PersonTransformer.transformPersonEntity(personEntitySaved);
    }

    @Override
    public void delete(Long id) {
        log.info("Trying to delete person by id {}", id);
        PersonEntity personEntity = findPersonEntityById(id);
        personRepository.delete(personEntity);
        log.info("Person deleted");
    }

    @Override
    public List<PersonDTO> findAll() {
        return null;
    }

    @Override
    public PersonDTO findById(Long id) {
        log.info("Trying to find person by id {}", id);
        PersonEntity personEntity = findPersonEntityById(id);
        log.info("Person found");
        return PersonTransformer.transformPersonEntity(personEntity);
    }
}
