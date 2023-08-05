package com.rurbisservices.churchdonation.service.model;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChurchDTO {
    private Long id;
    private String name = "";
    private String nameNew = "";
    private String street = "";
    private String number = "";
    private String details = "";
    private Timestamp creationDate;
    private Timestamp updateDate;

    public ChurchDTO(String nameNew, String street, String number, String details) {
        this.nameNew = nameNew;
        this.street = street;
        this.number = number;
        this.details = details;
    }

    public ChurchDTO(Long id, String name, String nameNew, String street, String number, String details) {
        this.id = id;
        this.name = name;
        this.nameNew = nameNew;
        this.street = street;
        this.number = number;
        this.details = details;
    }
}
