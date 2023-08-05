package com.rurbisservices.churchdonation.dao.repository;

import com.rurbisservices.churchdonation.dao.entity.ChurchEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IChurchRepository extends CrudRepository<ChurchEntity, Long> {
    List<ChurchEntity> findAllByOrderByName();

    List<ChurchEntity> findAllByNameIgnoreCaseOrderByName(String name);

    List<ChurchEntity> findAllByNameLikeIgnoreCaseOrderByName(String name);
}
