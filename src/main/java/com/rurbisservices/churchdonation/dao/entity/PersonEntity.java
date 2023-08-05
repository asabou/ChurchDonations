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
@Table(name = "persons")
public class PersonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name", length = 500)
    private String firstName = "";

    @Column(name = "last_name", length = 500)
    private String lastName = "";

    @Column(name = "details", length = 1000)
    private String details = "";

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "update_date")
    private Timestamp updateDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private HouseEntity house;
}
