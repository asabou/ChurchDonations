package com.rurbisservices.churchdonation.service.model;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class DonationTopicDTO {
    private Long id;
    private String topic = "";
    private String details = "";
    private String date = "";
    private Timestamp creationDate;
    private Timestamp updateDate;
    private Long churchId;

    public DonationTopicDTO(Long id, String topic, String details, Long churchId) {
        this.id = id;
        this.topic = topic;
        this.details = details;
        this.churchId = churchId;
    }

    public DonationTopicDTO(String topic, String details, Long churchId) {
        this.topic = topic;
        this.details = details;
        this.churchId = churchId;
    }
}
