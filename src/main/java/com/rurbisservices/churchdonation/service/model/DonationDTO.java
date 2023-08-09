package com.rurbisservices.churchdonation.service.model;

import com.rurbisservices.churchdonation.utils.DateUtils;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

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
    private List<SumeDonationTopicDTO> sumeDonationTopics;
    private String details = "";
    private Timestamp creationDate;
    private Timestamp updateDate;
    private Timestamp updateDateNew;
    private String date = "";
    private Long churchId;
    private String church = "";
    private Long houseId;
    private String house = "";
    private Long personId;
    private String person = "";

    public DonationDTO(Long id, String receipt, String receiptNew, String sume,
                       List<SumeDonationTopicDTO> sumeDonationTopics, String details,
                       LocalDate updateDate,
                       LocalDate updateDateNew,
                       Long churchId,
                       Long houseId,
                       Long personId) {
        this.id = id;
        this.receipt = receipt;
        this.receiptNew = receiptNew;
        this.sume = sume;
        this.details = details;
        this.sumeDonationTopics = sumeDonationTopics;
        this.updateDate = DateUtils.getFromTimestampFromLocalDate(updateDate);
        this.updateDateNew = DateUtils.getFromTimestampFromLocalDate(updateDateNew);
        this.churchId = churchId;
        this.houseId = houseId;
        this.personId = personId;
    }

    public DonationDTO(String receiptNew, String sume, List<SumeDonationTopicDTO> sumeDonationTopics, String details,
                       LocalDate updateDateNew, Long churchId,
                       Long houseId,
                       Long personId) {
        this.receiptNew = receiptNew;
        this.sume = sume;
        this.sumeDonationTopics = sumeDonationTopics;
        this.details = details;
        this.updateDateNew = DateUtils.getFromTimestampFromLocalDate(updateDateNew);
        this.churchId = churchId;
        this.houseId = houseId;
        this.personId = personId;
    }

    public DonationDTO copy() {
        return new DonationDTO(id, receipt, receiptNew, sume, donationTopics, sumeDonationTopics, details, creationDate, updateDate,
                updateDateNew, date,
                churchId,
                church, houseId,
                house, personId, person);
    }

}
