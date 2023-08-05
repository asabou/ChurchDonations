package com.rurbisservices.churchdonation.service.impl;

import com.rurbisservices.churchdonation.dao.repository.IChurchRepository;
import com.rurbisservices.churchdonation.dao.repository.IDonationRepository;
import com.rurbisservices.churchdonation.dao.repository.IHouseRepository;
import com.rurbisservices.churchdonation.service.model.HouseDTO;
import com.rurbisservices.churchdonation.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static com.rurbisservices.churchdonation.utils.Constants.EMPTY_STRING;

@Component
public class Test implements CommandLineRunner {
    @Autowired
    private IHouseRepository houseRepository;

    @Autowired
    private IChurchRepository churchRepository;

    @Autowired
    private HouseServiceImpl houseService;

    @Autowired
    private ChurchServiceImpl churchService;

    @Autowired
    private PersonServiceImpl personService;

    @Autowired
    private IDonationRepository donationRepository;

    @Override
    public void run(String... args) throws Exception {
//        System.out.println(donationRepository.getLastReceiptByChurchIdAndCurrentYear(1L));
//        donationRepository.findAllByChurchIdAndFilterOrderByReceipt(1L, "");
//        houseService.create(createHouse(1L, "street1", "1"));
//        houseService.create(createHouse(1L, "street2", "1"));
//        System.out.println(houseRepository.findAllByChurchIdOrderByStreetAscNumberAsc(1L));
//        System.out.println(houseService.findAll());
//        houseService.delete(1L);
//        System.out.println(houseService.findAll());
//        System.out.println(houseRepository.findAllByChurchIdOrderByStreetAscNumberAsc(1L));
//        System.out.println(churchService.findAll());
    }

    private HouseDTO createHouse(Long churchId, String street, String number) {
        HouseDTO house = new HouseDTO();
        house.setChurchId(churchId);
        house.setNumber(number);
        return house;
    }


}
