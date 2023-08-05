package com.rurbisservices.churchdonation.service.impl;

import com.rurbisservices.churchdonation.dao.entity.*;
import com.rurbisservices.churchdonation.service.abstracts.AbstractService;
import com.rurbisservices.churchdonation.service.abstracts.IChurchService;
import com.rurbisservices.churchdonation.service.helpers.ChurchTransformer;
import com.rurbisservices.churchdonation.service.model.ChurchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.rurbisservices.churchdonation.utils.DateUtils.getCurrentTimestamp;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.isListNullOrEmpty;

@Slf4j
@Service
public class ChurchServiceImpl extends AbstractService implements IChurchService {
    @Override
    public List<ChurchDTO> findAll() {
        log.info("Trying to find all churches ...");
        List<ChurchEntity> churches = churchRepository.findAllByOrderByName();
        log.info("Churches found: {}", churches.size());
        return ChurchTransformer.transformChurchEntities(churches);
    }

    @Override
    public ChurchDTO findById(Long id) {
        log.info("Trying to find church by id {}", id);
        ChurchEntity church = findChurchEntityById(id);
        log.info("Church found ... ");
        return ChurchTransformer.transformChurchEntity(church);
    }

    @Override
    public ChurchDTO create(ChurchDTO church) {
        church.setCreationDate(getCurrentTimestamp());
        church.setUpdateDate(getCurrentTimestamp());
        log.info("Trying to create a new church {}", church);
        churchValidation.validate(church);
        List<ChurchEntity> churches = churchRepository.findAllByNameIgnoreCaseOrderByName(church.getNameNew());
        if (!isListNullOrEmpty(churches)) {
            throwBadRequestException(102, church.getNameNew());
        }
        ChurchEntity churchEntityToSave = ChurchTransformer.transformChurchDTO(church);
        ChurchEntity churchEntitySaved = churchRepository.save(churchEntityToSave);
        log.info("Church created");
        return ChurchTransformer.transformChurchEntity(churchEntitySaved);
    }

    @Override
    public ChurchDTO update(ChurchDTO church) {
        church.setUpdateDate(getCurrentTimestamp());
        log.info("Trying to update church {}", church);
        churchValidation.validate(church);
        if (!church.getName().equals(church.getNameNew())) {
            List<ChurchEntity> churches = churchRepository.findAllByNameIgnoreCaseOrderByName(church.getNameNew());
            if (!isListNullOrEmpty(churches)) {
                throwBadRequestException(102, church.getName());
            }
        }
        ChurchEntity churchEntityToUpdate = findChurchEntityById(church.getId());
        ChurchTransformer.fillChurchEntity(church, churchEntityToUpdate);
        ChurchEntity churchEntityUpdated = churchRepository.save(churchEntityToUpdate);
        log.info("Church updated");
        return ChurchTransformer.transformChurchEntity(churchEntityUpdated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Trying to delete database records for church {}", id);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdOrderByReceipt(id);
        List<PersonEntity> personEntities = personRepository.findAllByChurchIdOrderByFirstNameAscLastNameAsc(id);
        List<HouseEntity> houseEntities = houseRepository.findAllByChurchIdOrderByNumber(id);
        List<DonationTopicEntity> donationTopicEntities = donationTopicRepository.findAllByChurchIdOrderByTopic(id);
        ChurchEntity churchEntity = findChurchEntityById(id);
        donationRepository.deleteAll(donationEntities);
        personRepository.deleteAll(personEntities);
        houseRepository.deleteAll(houseEntities);
        donationTopicRepository.deleteAll(donationTopicEntities);
        churchRepository.delete(churchEntity);
        log.info("Database cleanup finished for church {}", id);
    }


}
