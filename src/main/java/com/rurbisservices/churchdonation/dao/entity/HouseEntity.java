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
@Table(name = "houses")
public class HouseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "number", length = 100)
    private String number = "";

    @Column(name = "details", length = 1000)
    private String details = "";

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "update_date")
    private Timestamp updateDate;

//    @ManyToMany(mappedBy = "houses")
//    @ToString.Exclude
//    private List<ChurchEntity> churches = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    private ChurchEntity church;
}
