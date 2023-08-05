package com.rurbisservices.churchdonation.service.model;

import lombok.*;

import java.sql.Timestamp;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HouseDTO {
    private Long id;
    private String number = "";
    private String numberNew = "";
    private String details = "";
    private Timestamp creationDate;
    private Timestamp updateDate;
    private Long churchId;

    public HouseDTO(String numberNew, String details, Long churchId) {
        this.numberNew = numberNew;
        this.details = details;
        this.churchId = churchId;
    }

    public HouseDTO(Long id, String number, String numberNew, String details, Long churchId) {
        this.id = id;
        this.number = number;
        this.numberNew = numberNew;
        this.details = details;
        this.churchId = churchId;
    }

    public String displayFormat() {
        return this.numberNew;
    }
}
