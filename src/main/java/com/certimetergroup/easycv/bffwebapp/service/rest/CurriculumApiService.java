package com.certimetergroup.easycv.bffwebapp.service.rest;

import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.restclient.CurriculumApiClient;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumLightDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.create.CreateCurriculumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CurriculumApiService {

    private final CurriculumApiClient curriculumApiClient;

    public PagedResponseDto<CurriculumLightDto> getCurriculums(Integer page, Integer pageSize, Set<Long> userIds, Long domainId, Long domainOptionId) {
        return curriculumApiClient.getCurriculums(page, pageSize, userIds, domainId, domainOptionId);
    }

    public CurriculumDto getCurriculum(Long curriculumId) {
        return curriculumApiClient.getCurriculum(curriculumId);
    }

    public CurriculumDto addNewCurriculum(CreateCurriculumDto createCurriculumDto) {
        return curriculumApiClient.addNewCurriculum(createCurriculumDto);
    }

    public CurriculumDto replaceCurriculumData(Long curriculumId, CurriculumDto curriculumDto) {
        return curriculumApiClient.replaceCurriculumData(curriculumId, curriculumDto);
    }

    public void deleteCurriculum(Long curriculumId) {
        curriculumApiClient.deleteCurriculum(curriculumId);
    }
}