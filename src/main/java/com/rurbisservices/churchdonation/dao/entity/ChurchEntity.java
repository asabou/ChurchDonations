package com.rurbisservices.churchdonation.dao.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "churches")
public class ChurchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;

    @Column(name = "name", length = 500, unique = true, nullable = false)
    private String name = "";

    @Column(name = "street", length = 200)
    private String street = "";

    @Column(name = "number", length = 100)
    private String number = "";

    @Column(name = "details", length = 1000)
    private String details = "";

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "update_date")
    private Timestamp updateDate;

//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "churches_houses",
//            joinColumns = @JoinColumn(name = "church_id", referencedColumnName = "id"),
//            inverseJoinColumns = @JoinColumn(name = "house_id", referencedColumnName = "id")
//    )
//    @ToString.Exclude
//    private List<HouseEntity> houses = new ArrayList<>();

    @OneToMany(mappedBy = "church")
    @ToString.Exclude
    private List<HouseEntity> houses = new ArrayList<>();

    @OneToMany(mappedBy = "church")
    @ToString.Exclude
    private List<DonationTopicEntity> donationTopics = new ArrayList<>();
}
