package com.rurbisservices.churchdonation.utils;

import com.rurbisservices.churchdonation.dao.entity.ChurchEntity;
import com.rurbisservices.churchdonation.dao.repository.IChurchRepository;
import com.rurbisservices.churchdonation.dao.repository.IDonationRepository;
import com.rurbisservices.churchdonation.dao.repository.IHouseRepository;
import com.rurbisservices.churchdonation.dao.repository.IPersonRepository;
import com.rurbisservices.churchdonation.service.impl.*;
import com.rurbisservices.churchdonation.service.model.DonationDTO;
import com.rurbisservices.churchdonation.service.model.HouseDTO;
import com.rurbisservices.churchdonation.service.model.PersonDTO;
import com.rurbisservices.churchdonation.utils.exceptions.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;

import static com.rurbisservices.churchdonation.utils.ServiceUtils.isStringNullOrEmpty;

@Slf4j
@Component
public class DBCSVImport implements CommandLineRunner {
    @Autowired
    private ApplicationProperties properties;

    @Autowired
    private ChurchServiceImpl churchService;

    @Autowired
    private IChurchRepository churchRepository;

    @Autowired
    private IHouseRepository houseRepository;

    @Autowired
    private IDonationRepository donationRepository;

    @Autowired
    private IPersonRepository personRepository;

    @Autowired
    private HouseServiceImpl houseService;

    @Autowired
    private PersonServiceImpl personService;

    @Autowired
    private DonationTopicServiceImpl donationTopicService;

    @Autowired
    private DonationServiceImpl donationService;

    @Override
    public void run(String... args) throws Exception {
    }

    private void importInitialCSV() {
        personRepository.deleteAll();
        houseRepository.deleteAll();
        initDuplicatedHouses();
        ChurchEntity church = churchRepository.findAllByNameLikeIgnoreCaseOrderByName("%cizer%").get(0);
        try (BufferedReader reader = new BufferedReader(new FileReader(properties.getInitialDBCSV()))) {
            reader.readLine(); //skip header
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("Trying to import line {}", line);
                String [] params = line.trim().split(",");
                String id = params[0].trim();
                String number = params[1].trim();
                String yes = params[2].trim();
                String name = params[3].trim();
                String pers = params[4].trim();

                String numberExt = "";
                if (name.contains("/A")) {
                    numberExt = "/A";
                }
                if (name.contains("/B")) {
                    numberExt = "/B";
                }
                if (name.contains("/C")) {
                    numberExt = "/C";
                }
                number = number + numberExt;
                String firstName = "";
                String lastName = "";
                String []names = name.split(" ");
                if (!numberExt.equals("")) {
                    //names[0] should be /A or /B or /C
                    firstName = names[1];
                    for (int i = 2; i < names.length; i++) {
                        lastName = lastName + names[i];
                    }
                } else {
                    firstName = names[0];
                    for (int i = 1; i < names.length; i++) {
                        lastName = lastName + names[i];
                    }
                }

                HouseDTO house = new HouseDTO();
                house.setNumberNew(number);
                house.setChurchId(church.getId());
                house.setUpdateDate(DateUtils.getCurrentTimestamp());
                house.setCreationDate(DateUtils.getCurrentTimestamp());
                house.setDetails(!isStringNullOrEmpty(pers)? pers + " persoane" : "");

                try {
                    house = houseService.create(house);
                    PersonDTO person = new PersonDTO();
                    person.setFirstName(firstName);
                    person.setLastName(lastName);
                    person.setCreationDate(DateUtils.getCurrentTimestamp());
                    person.setUpdateDate(DateUtils.getCurrentTimestamp());
                    person.setHouseId(house.getId());
                    try {
                        person = personService.create(person);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                } catch (Exception e) {
                    appendToDuplicatedRecords(house);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void initDuplicatedHouses() {
        try {
            FileWriter writer = new FileWriter(properties.getInitialDBCSVDuplicatedRecords());
            writer.close();
        } catch (Exception e) {
            log.error("Error init duplicated records file");
            log.error(e.getMessage());
        }
    }

    private void appendToDuplicatedRecords(HouseDTO house) {
        try {
            FileWriter writer = new FileWriter(properties.getInitialDBCSVDuplicatedRecords(), true);
            writer.write(house.getNumberNew() + "," + house.getDetails() + "\n");
            writer.close();
        } catch (Exception e) {
            log.error("Error append to duplicated records");
            log.error(e.getMessage());
        }
    }




}
