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
@Table(name = "sume_donation_topics")
public class SumeDonationTopicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "topic", length = 100, nullable = false)
    private String topic;

    @Column(name = "sume", nullable = false)
    private Double sume;

    @Column(name = "creation_date", updatable = false)
    private Timestamp creationDate;

    @Column(name = "update_date")
    private Timestamp updateDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private DonationEntity donation;
}
