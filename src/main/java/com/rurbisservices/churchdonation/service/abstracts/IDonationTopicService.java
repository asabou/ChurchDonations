package com.rurbisservices.churchdonation.service.abstracts;

import com.rurbisservices.churchdonation.service.model.DonationTopicDTO;

import java.util.List;

public interface IDonationTopicService extends IService<DonationTopicDTO, Long> {
    List<DonationTopicDTO> findAllByChurchId(Long churchId);

    List<DonationTopicDTO> findAllByChurchIdAndFilter(Long churchId, String filter);
}
