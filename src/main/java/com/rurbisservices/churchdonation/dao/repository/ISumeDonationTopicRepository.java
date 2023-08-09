package com.rurbisservices.churchdonation.dao.repository;

import com.rurbisservices.churchdonation.dao.entity.SumeDonationTopicEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISumeDonationTopicRepository extends CrudRepository<SumeDonationTopicEntity, Long> {
    List<SumeDonationTopicEntity> findAllByDonationIdOrderByTopic(Long donationId);
}
