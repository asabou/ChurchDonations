package com.rurbisservices.churchdonation.service.model;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SumeDonationTopicDTO {
    private Long id;
    private String topic = "";
    private String sume = "";
    private Long donationId;

    public SumeDonationTopicDTO(String topic, String sume) {
        this.topic = topic;
        this.sume = sume;
    }

    public SumeDonationTopicDTO(String topic, String sume, Long donationId) {
        this.topic = topic;
        this.sume = sume;
        this.donationId = donationId;
    }

    public SumeDonationTopicDTO copy() {
        return new SumeDonationTopicDTO(id, topic, sume, donationId);
    }
}
