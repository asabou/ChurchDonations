package com.rurbisservices.churchdonation.service.abstracts;

import com.rurbisservices.churchdonation.service.model.PersonDTO;

import java.util.List;

public interface IPersonService extends IService<PersonDTO, Long> {
    List<PersonDTO> findAllByHouseId(Long houseId);
    List<PersonDTO> findAllByChurchId(Long churchId);
    List<PersonDTO> findAllByChurchIdAndFilter(Long churchId, String filter);
    List<PersonDTO> findAllByChurchIdAndHouseIdAndFilter(Long churchId, Long houseId, String filter);
}
