package com.certimetergroup.easycv.bffwebapp.service;

import com.certimetergroup.easycv.bffwebapp.dto.view.yourcv.CurriculumDetailDto;
import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.restclient.DomainApiClient;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.ProjectDomainOptionDto;
import com.certimetergroup.easycv.commons.response.dto.domain.CreateDomainDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainOptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DomainApiService {

    private final DomainApiClient domainApiClient;

    public PagedResponseDto<DomainDto> getDomains(Integer page, Integer pageSize, String domainName, String domainOptionValue) {
        return domainApiClient.getDomains(page, pageSize, domainName, domainOptionValue);
    }

    public DomainDto getDomain(Long domainId, Set<Long> domainOptionIds) {
        return domainApiClient.getDomain(domainId, domainOptionIds);
    }

    public DomainDto addNewDomain(CreateDomainDto createDomainDto) {
        return domainApiClient.addNewDomain(createDomainDto);
    }

    public DomainDto replaceDomainData(Long domainId, DomainDto domainDto) {
        return domainApiClient.replaceDomainData(domainId, domainDto);
    }

    public void deleteDomain(Long domainId) {
        domainApiClient.deleteDomain(domainId);
    }

    public DomainOptionDto getDomainOption(Long domainOptionId){ return domainApiClient.getDomainOption(domainOptionId); }

    public void getAllCurriculumDomains(CurriculumDetailDto curriculumDetailDto) {
        Map<Long, Set<Long>> domainRequirements = collectDomainRequirements(curriculumDetailDto.getCurriculum());

        Set<DomainDto> domains = domainRequirements.entrySet().stream()
                .map(entry -> {
                    Long domainId = entry.getKey();
                    Set<Long> optionIds = entry.getValue();
                    return domainApiClient.getDomain(domainId, optionIds);
                })
                .collect(Collectors.toSet());

        Set<DomainOptionDto> schools = new HashSet<>();
        Set<DomainOptionDto> degrees = new HashSet<>();
        curriculumDetailDto.getCurriculum().getEducationHistory().stream().forEach( entry -> {
            Long schoolId = entry.getSchoolNameId();
            DomainOptionDto school = domainApiClient.getDomainOption(schoolId);
            schools.add(school);
            Long degreeId = entry.getDegreeNameId();
            DomainOptionDto degree = domainApiClient.getDomainOption(degreeId);
            degrees.add(degree);
        });
        curriculumDetailDto.setDomains(domains);
        curriculumDetailDto.setSchools(schools);
        curriculumDetailDto.setDegrees(degrees);
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