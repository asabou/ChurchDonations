package com.rurbisservices.churchdonation.dao.repository;

import com.rurbisservices.churchdonation.dao.entity.DonationTopicEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDonationTopicRepository extends CrudRepository<DonationTopicEntity, Long> {
    List<DonationTopicEntity> findAllByChurchIdOrderByTopic(Long churchId);

    List<DonationTopicEntity> findAllByChurchIdAndTopicIgnoreCaseOrderByTopic(Long churchId, String topic);


    @Query("select dt from DonationTopicEntity dt where dt.church.id = :churchId and (" +
            "lower(dt.topic) like lower(:filter) or " +
            "lower(dt.details) like lower(:filter)) " +
            "order by dt.topic asc")
    List<DonationTopicEntity> findAllByChurchIdAndFilterOrderByTopic(Long churchId, String filter);
}
