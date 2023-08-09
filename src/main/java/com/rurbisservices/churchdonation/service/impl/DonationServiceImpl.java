package com.rurbisservices.churchdonation.service.impl;

import com.rurbisservices.churchdonation.dao.entity.DonationEntity;
import com.rurbisservices.churchdonation.dao.entity.HouseEntity;
import com.rurbisservices.churchdonation.dao.entity.PersonEntity;
import com.rurbisservices.churchdonation.dao.entity.SumeDonationTopicEntity;
import com.rurbisservices.churchdonation.service.abstracts.AbstractService;
import com.rurbisservices.churchdonation.service.abstracts.IDonationService;
import com.rurbisservices.churchdonation.service.helpers.DonationTransformer;
import com.rurbisservices.churchdonation.service.helpers.SumeDonationTopicTransformer;
import com.rurbisservices.churchdonation.service.model.DonationDTO;
import com.rurbisservices.churchdonation.service.model.SumeDonationTopicDTO;
import com.rurbisservices.churchdonation.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.rurbisservices.churchdonation.utils.DateUtils.getCurrentTimestamp;
import static com.rurbisservices.churchdonation.utils.ServiceUtils.*;

@Slf4j
@Service
public class DonationServiceImpl extends AbstractService implements IDonationService {
    @Override
    @Transactional
    public DonationDTO create(DonationDTO donationDTO) {
        donationDTO.setCreationDate(getCurrentTimestamp());
        donationDTO.setUpdateDate(donationDTO.getUpdateDateNew());
        log.info("Trying to create new Donation {}", donationDTO);
        donationValidation.validate(donationDTO);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndReceiptAndUpdateDateYear(donationDTO.getChurchId(),
                Long.parseLong(donationDTO.getReceiptNew()), donationDTO.getUpdateDateNew());
        if (!isListNullOrEmpty(donationEntities)) {
            throwBadRequestException(405, donationDTO.getReceipt());
        }
        List<SumeDonationTopicEntity> sumeDonationTopicEntities =
                SumeDonationTopicTransformer.transformSumeDonationTopicDTOS(donationDTO.getSumeDonationTopics());
        DonationEntity donationEntity = DonationTransformer.transformDonationDTO(donationDTO);
        donationEntity.setDonationTopics(getDonationTopicsFromSumeDonationTopics(sumeDonationTopicEntities));
        DonationEntity donationEntitySaved = donationRepository.save(donationEntity);
        List<SumeDonationTopicEntity> sumeDonationTopicEntitiesOld =
                sumeDonationTopicRepository.findAllByDonationIdOrderByTopic(donationEntitySaved.getId());
        sumeDonationTopicRepository.deleteAll(sumeDonationTopicEntitiesOld);
        sumeDonationTopicEntities.forEach(x -> x.setDonation(donationEntitySaved));
        sumeDonationTopicRepository.saveAll(sumeDonationTopicEntities);
        DonationDTO donation = DonationTransformer.transformDonationEntity(donationEntitySaved);
        addAdditionalInformationToDonation(donation);
        log.info("Donation created");
        return donation;
    }

    @Override
    @Transactional
    public DonationDTO update(DonationDTO donationDTO) {
        donationDTO.setUpdateDate(donationDTO.getUpdateDate());
        log.info("Trying to update donation {}", donationDTO);
        donationValidation.validate(donationDTO);
        validateDonationDTOWhenUpdate(donationDTO);
        DonationEntity donationEntityToUpdate = findDonationEntityById(donationDTO.getId());
        DonationTransformer.fillDonationEntity(donationDTO, donationEntityToUpdate);
        List<SumeDonationTopicEntity> sumeDonationTopicEntities =
                SumeDonationTopicTransformer.transformSumeDonationTopicDTOS(donationDTO.getSumeDonationTopics());
        donationEntityToUpdate.setDonationTopics(getDonationTopicsFromSumeDonationTopics(sumeDonationTopicEntities));
        DonationEntity donationEntitySaved = donationRepository.save(donationEntityToUpdate);
        List<SumeDonationTopicEntity> sumeDonationTopicEntitiesOld =
                sumeDonationTopicRepository.findAllByDonationIdOrderByTopic(donationEntitySaved.getId());
        sumeDonationTopicRepository.deleteAll(sumeDonationTopicEntitiesOld);
        sumeDonationTopicEntities.forEach(x -> x.setDonation(donationEntitySaved));
        sumeDonationTopicRepository.saveAll(sumeDonationTopicEntities);
        DonationDTO donation = DonationTransformer.transformDonationEntity(donationEntitySaved);
        addAdditionalInformationToDonation(donation);
        log.info("Donation updated");
        return donation;
    }

    @Override
    public void delete(Long id) {
        log.info("Trying to delete Donation by id {}", id);
        DonationEntity donationEntity = findDonationEntityById(id);
        List<SumeDonationTopicEntity> sumeDonationTopicEntities = sumeDonationTopicRepository.findAllByDonationIdOrderByTopic(donationEntity.getId());
        sumeDonationTopicRepository.deleteAll(sumeDonationTopicEntities);
        donationRepository.delete(donationEntity);
    }

    @Override
    public List<DonationDTO> findAll() {
        return null;
    }

    @Override
    public DonationDTO findById(Long aLong) {
        return null;
    }

    @Override
    public List<DonationDTO> findAllByChurchId(Long churchId) {
        log.info("Trying to find donations by churchId {}", churchId);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdOrderByReceipt(churchId);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addInformationToDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndFilter(Long churchId, String filter) {
        String filterWildCard = wildCardParam(filter);
        log.info("Trying to find donations by church {} and filter {}", churchId, filterWildCard);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndFilterOrderByReceipt(churchId, filterWildCard);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addInformationToDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndDateBetween(Long churchId, LocalDate dateFrom, LocalDate dateTo) {
        Timestamp dateFromTimestamp = DateUtils.getFromTimestampFromLocalDate(dateFrom);
        Timestamp dateToTimestamp = DateUtils.getToTimestampFromLocalDate(dateTo);
        log.info("Trying to find donations by church {} and dateFrom {}, dateTo {}", churchId, dateFromTimestamp, dateToTimestamp);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndDateBetweenOrderBySumeAndDate(churchId, dateFromTimestamp, dateToTimestamp);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addInformationToDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndHouseIdAndFilter(Long churchId, Long houseId, String filter) {
        String filterWildCard = wildCardParam(filter);
        log.info("Trying to find donations by church {}, house {} and filter {}", churchId, houseId, filterWildCard);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndHouseIdAndFilterOrderByReceipt(churchId, houseId, filterWildCard);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addInformationToDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndHouseIdAndDateBetween(Long churchId, Long houseId, LocalDate dateFrom, LocalDate dateTo) {
        Timestamp dateFromTimestamp = DateUtils.getFromTimestampFromLocalDate(dateFrom);
        Timestamp dateToTimestamp = DateUtils.getToTimestampFromLocalDate(dateTo);
        log.info("Trying to find donations by church {}, house {} and dateFrom {}, dateTo {}", churchId, houseId, dateFromTimestamp, dateToTimestamp);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndHouseIdAndDateBetweenOrderBySumeAndDate(churchId, houseId,
                dateFromTimestamp, dateToTimestamp);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addInformationToDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndPersonIdAndFilter(Long churchId, Long personId, String filter) {
        String filterWildCard = wildCardParam(filter);
        log.info("Trying to find donations by church {}, person {} and filter {}", churchId, personId, filterWildCard);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndPersonIdAndFilterOrderByReceipt(churchId, personId,
                filterWildCard);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addInformationToDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndPersonIdAndDateBetween(Long churchId, Long personId, LocalDate dateFrom, LocalDate dateTo) {
        Timestamp dateFromTimestamp = DateUtils.getFromTimestampFromLocalDate(dateFrom);
        Timestamp dateToTimestamp = DateUtils.getToTimestampFromLocalDate(dateTo);
        log.info("Trying to find donations by church {}, person {} and dateFrom {}, dateTo {}", churchId, personId, dateFromTimestamp, dateToTimestamp);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndPersonIdAndDateBetweenOrderBySumeAndDate(churchId, personId,
                dateFromTimestamp, dateToTimestamp);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addInformationToDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndHouseIdAndPersonIdAndFilter(Long churchId, Long houseId, Long personId, String filter) {
        String filterWildCard = wildCardParam(filter);
        log.info("Trying to find donations by church {}, house {}, person {} and filter {}", churchId, houseId, personId, filterWildCard);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndHouseIdAndPersonIdAndFilterOrderByReceipt(churchId, houseId,
                personId, filterWildCard);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addInformationToDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndHouseIdAndPersonIdAndDateBetween(Long churchId, Long houseId, Long personId, LocalDate dateFrom, LocalDate dateTo) {
        Timestamp dateFromTimestamp = DateUtils.getFromTimestampFromLocalDate(dateFrom);
        Timestamp dateToTimestamp = DateUtils.getToTimestampFromLocalDate(dateTo);
        log.info("Trying to find donations by church {}, house {}, person {} and dateFrom {}, dateTo {}", churchId, houseId, personId,
                dateFromTimestamp, dateToTimestamp);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndHouseIdAndPersonIdAndDateBetweenOrderBySumeAndDate(churchId,
                houseId, personId, dateFromTimestamp, dateToTimestamp);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addInformationToDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndHouseId(Long churchId, Long houseId) {
        log.info("Trying to find donations by churchId {}, houseId {}", churchId, houseId);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndHouseIdOrderByReceipt(churchId, houseId);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addInformationToDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndHouseIdAndPersonId(Long churchId, Long houseId, Long personId) {
        log.info("Trying to find donations by churchId {}, houseId {}", churchId, houseId);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndHouseIdAndPersonIdOrderByReceipt(churchId, houseId, personId);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addInformationToDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public Long getNextReceiptByChurchId(Long churchId) {
        Long lastReceipt = donationRepository.getLastReceiptByChurchIdAndCurrentYear(churchId, DateUtils.getCurrentTimestamp());
        if (isObjectNull(lastReceipt)) {
            return 1L;
        }
        return lastReceipt + 1;
    }

    private void addInformationToDonations(List<DonationDTO> donations) {
        donations.forEach(this::addAdditionalInformationToDonation);
    }

    private void addAdditionalInformationToDonation(DonationDTO donation) {
        Long houseId = donation.getHouseId();
        Long personId = donation.getPersonId();
        if (!isObjectNull(houseId)) {
            Optional<HouseEntity> houseOptional = houseRepository.findById(houseId);
            if (houseOptional.isPresent()) {
                HouseEntity house = houseOptional.get();
                donation.setHouse(house.getNumber());
            }
        }
        if (!isObjectNull(personId)) {
            Optional<PersonEntity> personOptional = personRepository.findById(personId);
            if (personOptional.isPresent()) {
                PersonEntity person = personOptional.get();
                donation.setPerson(person.getFirstName() + " " + person.getLastName());
            }
        }
        List<SumeDonationTopicEntity> sumeDonationTopicEntities = sumeDonationTopicRepository.findAllByDonationIdOrderByTopic(donation.getId());
        List<SumeDonationTopicDTO> sumeDonationTopicDTOS = SumeDonationTopicTransformer.transformSumeDonationTopicEntities(sumeDonationTopicEntities);
        donation.setSumeDonationTopics(sumeDonationTopicDTOS);
        donation.setDonationTopics(isStringNullOrEmpty(donation.getDonationTopics()) ?
                getDonationTopicsFromSumeDonationTopics(sumeDonationTopicEntities) : donation.getDonationTopics());
    }

    private void validateDonationDTOWhenUpdate(DonationDTO donationDTO) {
        log.info("Trying to validate donation when update ...");
        Long churchId = donationDTO.getChurchId();
        Long receipt = Long.parseLong(donationDTO.getReceipt());
        Long receiptNew = Long.parseLong(donationDTO.getReceiptNew());
        Timestamp updateDate = donationDTO.getUpdateDate();
        Timestamp updateDateNew = donationDTO.getUpdateDateNew();
        if (updateDate.equals(updateDateNew)) {
            if (!donationDTO.getReceipt().equals(donationDTO.getReceiptNew())) {
                List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndReceiptAndUpdateDateYear(churchId, receiptNew,
                        updateDateNew);
                if (!isListNullOrEmpty(donationEntities)) {
                    throwBadRequestException(405, receiptNew);
                }
            }
        }
        if (!donationDTO.getUpdateDate().equals(donationDTO.getUpdateDateNew())) {
            List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndReceiptAndUpdateDateYear(churchId, receiptNew,
                    updateDateNew);
            if (!isListNullOrEmpty(donationEntities)) {
                throwBadRequestException(405, receiptNew);
            }
        }
    }


    private String getDonationTopicsFromSumeDonationTopics(List<SumeDonationTopicEntity> sumeDonationTopicEntities) {
        String details = "";
        for (int i = 0; i < sumeDonationTopicEntities.size(); i++) {
            if (i == sumeDonationTopicEntities.size() - 1) {
                details = details + sumeDonationTopicEntities.get(i).getTopic() + " - " + String.format("%.0f",
                        sumeDonationTopicEntities.get(i).getSume());
            } else {
                details = details + sumeDonationTopicEntities.get(i).getTopic() + " - " +  String.format("%.0f",
                        sumeDonationTopicEntities.get(i).getSume()) + "\n";
            }
        }
        return details;
    }
}