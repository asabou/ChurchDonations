package com.rurbisservices.churchdonation.service.abstracts;

import com.rurbisservices.churchdonation.dao.entity.*;
import com.rurbisservices.churchdonation.dao.repository.*;
import com.rurbisservices.churchdonation.utils.ApplicationProperties;
import com.rurbisservices.churchdonation.utils.MessagesUtils;
import com.rurbisservices.churchdonation.utils.exceptions.BadRequestException;
import com.rurbisservices.churchdonation.utils.exceptions.NotFoundException;
import com.rurbisservices.churchdonation.validators.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public abstract class AbstractService {
    @Autowired
    protected ApplicationProperties applicationProperties;

    @Autowired
    protected IChurchRepository churchRepository;

    @Autowired
    protected IHouseRepository houseRepository;

    @Autowired
    protected IPersonRepository personRepository;

    @Autowired
    protected IDonationRepository donationRepository;

    @Autowired
    protected IDonationTopicRepository donationTopicRepository;

    @Autowired
    protected ChurchValidation churchValidation;

    @Autowired
    protected HouseValidation houseValidation;

    @Autowired
    protected PersonValidation personValidation;

    @Autowired
    protected DonationValidation donationValidation;

    @Autowired
    protected DonationTopicValidation donationTopicValidation;

    public void throwNotFoundException(Integer code, Object ...params) {
        String message = MessagesUtils.getMessage(code);
        throw new NotFoundException(String.format(message, params));
    }

    public void throwBadRequestException(Integer code, Object ...params) {
        String message = MessagesUtils.getMessage(code);
        throw new BadRequestException(String.format(message, params));
    }

    public ChurchEntity findChurchEntityById(Long id) {
        Optional<ChurchEntity> churchOptional = churchRepository.findById(id);
        if (!churchOptional.isPresent()) {
            throwNotFoundException(101, id);
        }
        return churchOptional.get();
    }

    public HouseEntity findHouseEntityById(Long id) {
        Optional<HouseEntity> houseOptional = houseRepository.findById(id);
        if (!houseOptional.isPresent()) {
            throwNotFoundException(201, id);
        }
        return  houseOptional.get();
    }

    public PersonEntity findPersonEntityById(Long id) {
        Optional<PersonEntity> personOptional = personRepository.findById(id);
        if (!personOptional.isPresent()) {
            throwNotFoundException(301, id);
        }
        return personOptional.get();
    }

    public DonationEntity findDonationEntityById(Long id) {
        Optional<DonationEntity> donationOptional = donationRepository.findById(id);
        if (!donationOptional.isPresent()) {
            throwNotFoundException(401, id);
        }
        return donationOptional.get();
    }

    public DonationTopicEntity findDonationTopicEntityById(Long id) {
        Optional<DonationTopicEntity> donationTopicOptional = donationTopicRepository.findById(id);
        if (!donationTopicOptional.isPresent()) {
            throwNotFoundException(501, id);
        }
        return donationTopicOptional.get();
    }
}
