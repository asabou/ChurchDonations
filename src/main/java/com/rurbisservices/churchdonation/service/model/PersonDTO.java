package com.rurbisservices.churchdonation.service.model;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PersonDTO {
    private Long id;
    private String firstName = "";
    private String lastName = "";
    private String details = "";
    private Timestamp creationDate;
    private Timestamp updateDate;
    private Long houseId;
    private String house = "";

    public PersonDTO(String firstName, String lastName, String details, Long houseId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.details = details;
        this.houseId = houseId;
    }

    public PersonDTO(Long id, String firstName, String lastName, String details, Long houseId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.details = details;
        this.houseId = houseId;
    }

    public String displayFormat() {
        return firstName + " " + lastName;
    }
}
