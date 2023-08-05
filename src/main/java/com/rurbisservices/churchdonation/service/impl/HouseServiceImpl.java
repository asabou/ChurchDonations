package com.rurbisservices.churchdonation.service.impl;

import com.rurbisservices.churchdonation.dao.entity.ChurchEntity;
import com.rurbisservices.churchdonation.dao.entity.HouseEntity;
import com.rurbisservices.churchdonation.dao.entity.PersonEntity;
import com.rurbisservices.churchdonation.service.abstracts.AbstractService;
import com.rurbisservices.churchdonation.service.abstracts.IHouseService;
import com.rurbisservices.churchdonation.service.helpers.HouseTransformer;
import com.rurbisservices.churchdonation.service.model.HouseDTO;
import com.rurbisservices.churchdonation.utils.ServiceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.rurbisservices.churchdonation.utils.DateUtils.getCurrentTimestamp;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.isListNullOrEmpty;

@Slf4j
@Service
public class HouseServiceImpl extends AbstractService implements IHouseService {
    @Override
    public List<HouseDTO> findAllByChurchId(Long id) {
        log.info("Trying to find houses from church {}", id);
        List<HouseEntity> houseEntities = houseRepository.findAllByChurchIdOrderByNumber(id);
        List<HouseDTO> houseDTOS = HouseTransformer.transformHouseEntities(houseEntities);
        log.info("Houses found: {}", houseDTOS.size());
        return houseDTOS;
    }

    @Override
    public List<HouseDTO> searchBy(Long churchId, String filter) {
        String filterWildCard = ServiceUtils.wildCardParam(filter);
        log.info("Trying to search houses by church {} and filter {}", churchId, filterWildCard);
        List<HouseEntity> houseEntities = houseRepository.findAllByChurchIdAndFilterOrderByNumberAsc(churchId, filterWildCard);
        List<HouseDTO> houseDTOS = HouseTransformer.transformHouseEntities(houseEntities);
        log.info("Houses found: {}", houseDTOS.size());
        return houseDTOS;
    }

    @Override
    public List<HouseDTO> searchByChurchIdAndFilterPerson(Long churchId, String filter) {
        String filterWildCard = ServiceUtils.wildCardParam(filter);
        log.info("Trying to search by church {} and filter person {}", churchId, filterWildCard);
        List<HouseEntity> houseEntities = houseRepository.findAllByChurchIdAndFilterPersonOrderByNumber(churchId, filterWildCard);
        List<HouseDTO> houseDTOS = HouseTransformer.transformHouseEntities(houseEntities);
        log.info("Houses found {}", houseDTOS.size());
        return houseDTOS;
    }

    @Override
    @Transactional
    public HouseDTO create(HouseDTO houseDTO) {
        houseDTO.setCreationDate(getCurrentTimestamp());
        houseDTO.setUpdateDate(getCurrentTimestamp());
        log.info("Trying to create a new house {}", houseDTO);
        houseValidation.validate(houseDTO);
        ChurchEntity churchEntity = findChurchEntityById(houseDTO.getChurchId());
        List<HouseEntity> houses = houseRepository.findAllByChurchIdAndNumberOrderByNumber(houseDTO.getChurchId(), houseDTO.getNumberNew());
        if (!isListNullOrEmpty(houses)) {
            throwBadRequestException(203, houseDTO.getNumberNew());
        }
        HouseEntity houseEntity = HouseTransformer.transformHouseDTO(houseDTO);
        houseEntity.setChurch(churchEntity);
        HouseEntity houseSaved = houseRepository.save(houseEntity);
        HouseDTO houseToReturn = HouseTransformer.transformHouseEntity(houseSaved);
        log.info("House created");
        return houseToReturn;
    }

    @Override
    public HouseDTO update(HouseDTO houseDTO) {
        houseDTO.setUpdateDate(getCurrentTimestamp());
        log.info("Trying to update house {}", houseDTO);
        houseValidation.validate(houseDTO);
        if (!houseDTO.getNumber().equals(houseDTO.getNumberNew())) {
            List<HouseEntity> houses = houseRepository.findAllByChurchIdAndNumberOrderByNumber(houseDTO.getChurchId(), houseDTO.getNumberNew());
            if (!isListNullOrEmpty(houses)) {
                throwBadRequestException(203, houseDTO.getNumberNew());
            }
        }
        HouseEntity houseEntityToUpdate = findHouseEntityById(houseDTO.getId());
        HouseTransformer.fillHouseEntity(houseDTO, houseEntityToUpdate);
        HouseEntity houseEntitySaved = houseRepository.save(houseEntityToUpdate);
        HouseDTO house = HouseTransformer.transformHouseEntity(houseEntitySaved);
        log.info("House updated");
        return house;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Trying to delete house by id {}", id);
        List<PersonEntity> personEntities = personRepository.findAllByHouse_IdOrderByFirstName(id);
        personRepository.deleteAll(personEntities);
        HouseEntity houseEntity = findHouseEntityById(id);
        houseRepository.delete(houseEntity);
        log.info("House and persons deleted");
    }

    @Override
    public List<HouseDTO> findAll() {
        log.info("Trying to find all houses ...");
        List<HouseEntity> houseEntities = houseRepository.findAll();
        log.info("Houses found: {}", houseEntities.size());
        return HouseTransformer.transformHouseEntities(houseEntities);
    }

    @Override
    public HouseDTO findById(Long id) {
        log.info("Trying to find house by id {}", id);
        HouseEntity houseEntity = findHouseEntityById(id);
        HouseDTO houseDTO = HouseTransformer.transformHouseEntity(houseEntity);
        log.info("House found");
        return houseDTO;
    }
}
