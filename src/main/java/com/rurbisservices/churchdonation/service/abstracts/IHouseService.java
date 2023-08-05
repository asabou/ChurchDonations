package com.rurbisservices.churchdonation.service.abstracts;

import com.rurbisservices.churchdonation.service.model.HouseDTO;

import java.util.List;

public interface IHouseService extends IService<HouseDTO, Long> {
   List<HouseDTO> findAllByChurchId(Long id);
   List<HouseDTO> searchBy(Long churchId, String filter);
   List<HouseDTO> searchByChurchIdAndFilterPerson(Long churchId, String filter);
}
