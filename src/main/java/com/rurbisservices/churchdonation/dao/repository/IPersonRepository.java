package com.rurbisservices.churchdonation.dao.repository;

import com.rurbisservices.churchdonation.dao.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPersonRepository extends JpaRepository<PersonEntity, Long> {
    List<PersonEntity> findAllByOrderByFirstName();

    List<PersonEntity> findAllByHouse_IdOrderByFirstName(Long id);

    @Query("select p from PersonEntity p where p.house.id = :houseId and p.house.church.id = :churchId and " +
            "(lower(p.firstName) like lower(:filter) or lower(p.lastName) like lower(:filter) or lower(p.details) like lower(:filter)) " +
            "order by p.firstName asc, p.lastName asc")
    List<PersonEntity> findAllByHouseIdAndChurchIdAndFilterOrderByFirstNameAscLastNameAsc(Long houseId, Long churchId, String filter);

    @Query("select p from PersonEntity p where p.house.church.id = :churchId " +
            "order by p.firstName asc, p.lastName asc")
    List<PersonEntity> findAllByChurchIdOrderByFirstNameAscLastNameAsc(Long churchId);

    @Query("select p from PersonEntity p where p.house.church.id = :churchId and " +
            "(lower(p.firstName) like lower(:filter) or lower(p.lastName) like lower(:filter) or lower(p.details) like lower(:filter)) " +
            "order by p.firstName asc, p.lastName asc")
    List<PersonEntity> findAllByChurchIdAndFilterOrderByFirstNameAscLastNameAsc(Long churchId, String filter);
}
