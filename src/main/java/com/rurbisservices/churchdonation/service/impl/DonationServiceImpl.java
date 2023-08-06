package com.rurbisservices.churchdonation.service.impl;

import com.rurbisservices.churchdonation.dao.entity.DonationEntity;
import com.rurbisservices.churchdonation.dao.entity.HouseEntity;
import com.rurbisservices.churchdonation.dao.entity.PersonEntity;
import com.rurbisservices.churchdonation.service.abstracts.AbstractService;
import com.rurbisservices.churchdonation.service.abstracts.IDonationService;
import com.rurbisservices.churchdonation.service.helpers.DonationTransformer;
import com.rurbisservices.churchdonation.service.model.DonationDTO;
import com.rurbisservices.churchdonation.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Time;
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
        DonationEntity donationEntity = DonationTransformer.transformDonationDTO(donationDTO);
        DonationEntity donationEntitySaved = donationRepository.save(donationEntity);
        DonationDTO donation = DonationTransformer.transformDonationEntity(donationEntitySaved);
        addHouseAndPersonToDonation(donation);
        log.info("Donation created");
        return donation;
    }

    @Override
    public DonationDTO update(DonationDTO donationDTO) {
        donationDTO.setUpdateDate(donationDTO.getUpdateDate());
        log.info("Trying to update donation {}", donationDTO);
        donationValidation.validate(donationDTO);
        validateDonationDTOWhenUpdate(donationDTO);
        DonationEntity donationEntityToUpdate = findDonationEntityById(donationDTO.getId());
        DonationTransformer.fillDonationEntity(donationDTO, donationEntityToUpdate);
        DonationEntity donationEntitySaved = donationRepository.save(donationEntityToUpdate);
        DonationDTO donation = DonationTransformer.transformDonationEntity(donationEntitySaved);
        addHouseAndPersonToDonation(donation);
        log.info("Donation updated");
        return donation;
    }

    @Override
    public void delete(Long id) {
        log.info("Trying to delete Donation by id {}", id);
        DonationEntity donationEntity = findDonationEntityById(id);
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
        addHouseAndPersonToListOfDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndFilter(Long churchId, String filter) {
        String filterWildCard = wildCardParam(filter);
        log.info("Trying to find donations by church {} and filter {}", churchId, filterWildCard);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndFilterOrderByReceipt(churchId, filterWildCard);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addHouseAndPersonToListOfDonations(donationDTOS);
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
        addHouseAndPersonToListOfDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndHouseIdAndFilter(Long churchId, Long houseId, String filter) {
        String filterWildCard = wildCardParam(filter);
        log.info("Trying to find donations by church {}, house {} and filter {}", churchId, houseId, filterWildCard);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndHouseIdAndFilterOrderByReceipt(churchId, houseId, filterWildCard);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addHouseAndPersonToListOfDonations(donationDTOS);
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
        addHouseAndPersonToListOfDonations(donationDTOS);
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
        addHouseAndPersonToListOfDonations(donationDTOS);
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
        addHouseAndPersonToListOfDonations(donationDTOS);
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
        addHouseAndPersonToListOfDonations(donationDTOS);
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
        addHouseAndPersonToListOfDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndHouseId(Long churchId, Long houseId) {
        log.info("Trying to find donations by churchId {}, houseId {}", churchId, houseId);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndHouseIdOrderByReceipt(churchId, houseId);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addHouseAndPersonToListOfDonations(donationDTOS);
        return donationDTOS;
    }

    @Override
    public List<DonationDTO> findAllByChurchIdAndHouseIdAndPersonId(Long churchId, Long houseId, Long personId) {
        log.info("Trying to find donations by churchId {}, houseId {}", churchId, houseId);
        List<DonationEntity> donationEntities = donationRepository.findAllByChurchIdAndHouseIdAndPersonIdOrderByReceipt(churchId, houseId, personId);
        log.info("Donations found {}", donationEntities.size());
        List<DonationDTO> donationDTOS = DonationTransformer.transformDonationEntities(donationEntities);
        addHouseAndPersonToListOfDonations(donationDTOS);
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

    private void addHouseAndPersonToListOfDonations(List<DonationDTO> donations) {
        donations.forEach(this::addHouseAndPersonToDonation);
    }

    private void addHouseAndPersonToDonation(DonationDTO donation) {
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
}