package com.rurbisservices.churchdonation.dao.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "donations")
public class DonationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "receipt", length = 200, nullable = false)
    private Long receipt;

    @Column(name = "sume", nullable = false)
    private Double sume;

    @Column(name = "donation_topics", length = 1000)
    private String donationTopics = "";

    @Column(name = "details", length = 1000)
    private String details = "";

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "update_date")
    private Timestamp updateDate;

    @Column(name = "church_id", nullable = false)
    private Long churchId;

    @Column(name = "person_id")
    private Long personId;

    @Column(name = "house_id", nullable = false)
    private Long houseId;
}
