package com.rurbisservices.churchdonation.service.impl;

import com.rurbisservices.churchdonation.dao.entity.ChurchEntity;
import com.rurbisservices.churchdonation.dao.entity.DonationTopicEntity;
import com.rurbisservices.churchdonation.service.abstracts.AbstractService;
import com.rurbisservices.churchdonation.service.abstracts.IDonationTopicService;
import com.rurbisservices.churchdonation.service.helpers.DonationTopicTransformer;
import com.rurbisservices.churchdonation.service.model.DonationTopicDTO;
import com.rurbisservices.churchdonation.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.isListNullOrEmpty;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.wildCardParam;

@Slf4j
@Service
public class DonationTopicServiceImpl extends AbstractService implements IDonationTopicService {
    @Override
    public List<DonationTopicDTO> findAllByChurchId(Long churchId) {
        log.info("Trying to findAll DonationTopics by churchId {}", churchId);
        List<DonationTopicEntity> donationTopicEntities = donationTopicRepository.findAllByChurchIdOrderByTopic(churchId);
        log.info("DonationTopics found {}", donationTopicEntities.size());
        return DonationTopicTransformer.transformDonationTopicEntities(donationTopicEntities);
    }

    @Override
    public List<DonationTopicDTO> findAllByChurchIdAndFilter(Long churchId, String filter) {
        String filterWildCard = wildCardParam(filter);
        log.info("Trying to find all DonationTopics by church {} and filter {}", churchId, filterWildCard);
        List<DonationTopicEntity> donationTopicEntities = donationTopicRepository.findAllByChurchIdAndFilterOrderByTopic(churchId, filterWildCard);
        log.info("DonationTopics found {}", donationTopicEntities.size());
        return DonationTopicTransformer.transformDonationTopicEntities(donationTopicEntities);
    }

    @Override
    public DonationTopicDTO create(DonationTopicDTO donationTopicDTO) {
        donationTopicDTO.setCreationDate(DateUtils.getCurrentTimestamp());
        donationTopicDTO.setUpdateDate(DateUtils.getCurrentTimestamp());
        log.info("Trying to create new DonationTopic {}", donationTopicDTO);
        donationTopicValidation.validate(donationTopicDTO);
        List<DonationTopicEntity> donationTopicEntities =
                donationTopicRepository.findAllByChurchIdAndTopicIgnoreCaseOrderByTopic(donationTopicDTO.getChurchId(), donationTopicDTO.getTopic());
        if (!isListNullOrEmpty(donationTopicEntities)) {
            throwBadRequestException(502);
        }
        DonationTopicEntity donationTopicEntity = DonationTopicTransformer.transformDonationTopicDTO(donationTopicDTO);
        ChurchEntity churchEntity = findChurchEntityById(donationTopicDTO.getChurchId());
        donationTopicEntity.setChurch(churchEntity);
        DonationTopicEntity donationTopicEntitySaved = donationTopicRepository.save(donationTopicEntity);
        log.info("Donation topic created");
        return DonationTopicTransformer.transformDonationTopicEntity(donationTopicEntitySaved);
    }

    @Override
    public DonationTopicDTO update(DonationTopicDTO donationTopicDTO) {
        donationTopicDTO.setUpdateDate(DateUtils.getCurrentTimestamp());
        log.info("Trying to update Donation Topic {}", donationTopicDTO);
        donationTopicValidation.validate(donationTopicDTO);
        DonationTopicEntity donationTopicEntity = findDonationTopicEntityById(donationTopicDTO.getId());
        DonationTopicTransformer.fillDonationTopicEntity(donationTopicDTO, donationTopicEntity);
        DonationTopicEntity donationTopicEntitySaved = donationTopicRepository.save(donationTopicEntity);
        log.info("Donation topic updated");
        return DonationTopicTransformer.transformDonationTopicEntity(donationTopicEntitySaved);
    }

    @Override
    public void delete(Long id) {
        log.info("Trying to delete donation topic by id {}", id);
        DonationTopicEntity donationTopicEntity = findDonationTopicEntityById(id);
        donationTopicRepository.delete(donationTopicEntity);
    }

    @Override
    public List<DonationTopicDTO> findAll() {
        return null;
    }

    @Override
    public DonationTopicDTO findById(Long aLong) {
        return null;
    }
}
