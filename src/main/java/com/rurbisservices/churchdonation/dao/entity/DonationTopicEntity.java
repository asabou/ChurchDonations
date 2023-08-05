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
@Table(name = "donation_topics")
public class DonationTopicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "topic", nullable = false, length = 100)
    private String topic = "";

    @Column(name = "details", length = 1000)
    private String details = "";

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "update_date")
    private Timestamp updateDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private ChurchEntity church;
}
