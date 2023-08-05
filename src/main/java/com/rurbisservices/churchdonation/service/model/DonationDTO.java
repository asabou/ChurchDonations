package com.rurbisservices.churchdonation.service.model;

import com.rurbisservices.churchdonation.utils.DateUtils;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DonationDTO {
    private Long id;
    private String receipt = "";
    private String receiptNew = "";
    private String sume = "";
    private String donationTopics = "";
    private String details = "";
    private Timestamp creationDate;
    private Timestamp updateDate;
    private String date = "";
    private Long churchId;
    private String church = "";
    private Long houseId;
    private String house = "";
    private Long personId;
    private String person = "";

    public DonationDTO(Long id, String receipt, String receiptNew, String sume, String donationTopics, String details, LocalDate updateDate,
                       Long churchId,
                       Long houseId,
                       Long personId) {
        this.id = id;
        this.receipt = receipt;
        this.receiptNew = receiptNew;
        this.sume = sume;
        this.donationTopics = donationTopics;
        this.details = details;
        this.updateDate = DateUtils.getFromTimestampFromLocalDate(updateDate);
        this.churchId = churchId;
        this.houseId = houseId;
        this.personId = personId;
    }

    public DonationDTO(String receiptNew, String sume, String donationTopics, String details, LocalDate updateDate, Long churchId, Long houseId,
                       Long personId) {
        this.receiptNew = receiptNew;
        this.sume = sume;
        this.donationTopics = donationTopics;
        this.details = details;
        this.updateDate = DateUtils.getFromTimestampFromLocalDate(updateDate);
        this.churchId = churchId;
        this.houseId = houseId;
        this.personId = personId;
    }
}
