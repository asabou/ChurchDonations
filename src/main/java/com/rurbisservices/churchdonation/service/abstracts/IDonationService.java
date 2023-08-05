package com.rurbisservices.churchdonation.service.abstracts;

import com.rurbisservices.churchdonation.service.model.DonationDTO;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public interface IDonationService extends IService<DonationDTO, Long> {
    List<DonationDTO> findAllByChurchId(Long churchId);

    List<DonationDTO> findAllByChurchIdAndFilter(Long churchId, String filter);

    List<DonationDTO> findAllByChurchIdAndDateBetween(Long churchId, LocalDate dateFrom, LocalDate dateTo);

    List<DonationDTO> findAllByChurchIdAndHouseIdAndFilter(Long churchId, Long houseId, String filter);

    List<DonationDTO> findAllByChurchIdAndHouseIdAndDateBetween(Long churchId, Long houseId, LocalDate dateFrom, LocalDate dateTo);

    List<DonationDTO> findAllByChurchIdAndPersonIdAndFilter(Long churchId, Long personId, String filter);

    List<DonationDTO> findAllByChurchIdAndPersonIdAndDateBetween(Long churchId, Long personId, LocalDate dateFrom, LocalDate dateTo);

    List<DonationDTO> findAllByChurchIdAndHouseIdAndPersonIdAndFilter(Long churchId, Long houseId, Long personId, String filter);

    List<DonationDTO> findAllByChurchIdAndHouseIdAndPersonIdAndDateBetween(Long churchId, Long houseId, Long personId, LocalDate dateFrom, LocalDate dateTo);

    List<DonationDTO> findAllByChurchIdAndHouseId(Long churchId, Long houseId);

    List<DonationDTO> findAllByChurchIdAndHouseIdAndPersonId(Long churchId, Long houseId, Long personId);

    Long getNextReceiptByChurchId(Long churchId);
}
