package com.certimetergroup.easycv.bffwebapp.service;

import com.certimetergroup.easycv.bffwebapp.dto.CurriculumDetailDto;
import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.restclient.CurriculumApiClient;
import com.certimetergroup.easycv.bffwebapp.restclient.DomainApiClient;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumLightDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.ProjectDomainOptionDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.create.CreateCurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CurriculumApiService {

    private final CurriculumApiClient curriculumApiClient;
    private final DomainApiClient domainApiClient;

    public PagedResponseDto<CurriculumLightDto> getCurriculums(Integer page, Integer pageSize, Set<Long> userIds, Long domainId, Long domainOptionId) {
        return curriculumApiClient.getCurriculums(page, pageSize, userIds, domainId, domainOptionId);
    }

    public Optional<CurriculumDto> getCurriculum(Long curriculumId) {
        return curriculumApiClient.getCurriculum(curriculumId);
    }

    public Optional<CurriculumDetailDto> getCurriculumDetails(Long curriculumId) {
        Optional<CurriculumDto> optionalCurriculum = curriculumApiClient.getCurriculum(curriculumId);
        if (optionalCurriculum.isEmpty()) {
            return Optional.empty();
        }
        CurriculumDto curriculum = optionalCurriculum.get();

        Map<Long, Set<Long>> domainRequirements = collectDomainRequirements(curriculum);

        Set<DomainDto> domains = domainRequirements.entrySet().stream()
                .map(entry -> {
                    Long domainId = entry.getKey();
                    Set<Long> optionIds = entry.getValue();
                    return domainApiClient.getDomain(domainId, optionIds);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        CurriculumDetailDto detailDto = CurriculumDetailDto.builder()
                .curriculum(curriculum)
                .domains(domains)
                .build();

        return Optional.of(detailDto);
    }


    public CurriculumDto addNewCurriculum(CreateCurriculumDto createCurriculumDto) {
        return curriculumApiClient.addNewCurriculum(createCurriculumDto);
    }

    public Optional<CurriculumDto> replaceCurriculumData(Long curriculumId, CurriculumDto curriculumDto) {
        return curriculumApiClient.replaceCurriculumData(curriculumId, curriculumDto);
    }

    public void deleteCurriculum(Long curriculumId) {
        curriculumApiClient.deleteCurriculum(curriculumId);
    }

    private Map<Long, Set<Long>> collectDomainRequirements(CurriculumDto curriculum) {

        Stream<ProjectDomainOptionDto> personalSkillsStream = Optional.ofNullable(curriculum.getDomainOptions()) //
                .orElse(Set.of())
                .stream();

        Stream<ProjectDomainOptionDto> projectSkillsStream = Optional.ofNullable(curriculum.getProjects()) //
                .orElse(Set.of())
                .stream()
                .map(project -> Optional.ofNullable(project.getDomainOptions()).orElse(Set.of()))
                .flatMap(Set::stream);

        Stream<ProjectDomainOptionDto> allOptionsStream = Stream.concat(personalSkillsStream, projectSkillsStream)
                .filter(Objects::nonNull);

        return allOptionsStream.collect(
                Collectors.groupingBy(
                        ProjectDomainOptionDto::getDomainId,
                        Collectors.mapping(
                                ProjectDomainOptionDto::getDomainOptionId,
                                Collectors.toSet()
                        )
                )
        );
    }
}