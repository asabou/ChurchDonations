package com.rurbisservices.churchdonation.dao.repository;

import com.rurbisservices.churchdonation.dao.entity.HouseEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHouseRepository extends CrudRepository<HouseEntity, Long> {
    List<HouseEntity> findAll();

    @Query("select h from HouseEntity h where h.church.id = :id order by h.number asc")
    List<HouseEntity> findAllByChurchIdOrderByNumber(Long id);

    @Query("select h from HouseEntity h where h.church.id = :churchId and (lower(h.number) like lower(:filter) or " +
            "lower(h.details) like lower(:filter)) order by h.number asc")
    List<HouseEntity> findAllByChurchIdAndFilterOrderByNumberAsc(Long churchId, String filter);

    @Query("select h from HouseEntity h where h.church.id = :churchId and h.number = :number order by h.number asc")
    List<HouseEntity> findAllByChurchIdAndNumberOrderByNumber(Long churchId, String number);

    @Query("select distinct p.house from PersonEntity p where p.house.church.id = :churchId and (" +
            "lower(p.firstName) like lower(:filter) or " +
            "lower(p.lastName) like lower(:filter)) " +
            "order by p.house.number")
    List<HouseEntity> findAllByChurchIdAndFilterPersonOrderByNumber(Long churchId, String filter);
}
