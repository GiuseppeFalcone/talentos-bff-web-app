package com.certimetergroup.easycv.bffwebapp.service.rest;

import com.certimetergroup.easycv.bffwebapp.dto.view.yourcv.CurriculumDetailDto;
import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.restclient.DomainApiClient;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.ProjectDomainOptionDto;
import com.certimetergroup.easycv.commons.response.dto.domain.CreateDomainDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainOptionDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserDto;
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
        Map<Long, Set<Long>> domainRequirements = collectDomainRequirements(curriculumDetailDto);

        Set<DomainDto> domains = domainRequirements.entrySet().stream()
                .map(entry -> {
                    Long domainId = entry.getKey();
                    Set<Long> optionIds = entry.getValue();
                    return domainApiClient.getDomain(domainId, optionIds);
                })
                .collect(Collectors.toSet());

        Set<DomainOptionDto> schools = new HashSet<>();
        Set<DomainOptionDto> degrees = new HashSet<>();

        if (curriculumDetailDto.getCurriculum() != null && curriculumDetailDto.getCurriculum().getEducationHistory() != null) {
            curriculumDetailDto.getCurriculum().getEducationHistory().forEach(entry -> {
                if (entry.getSchoolNameId() != null) {
                    schools.add(domainApiClient.getDomainOption(entry.getSchoolNameId()));
                }
                if (entry.getDegreeNameId() != null) {
                    degrees.add(domainApiClient.getDomainOption(entry.getDegreeNameId()));
                }
            });
        }

        curriculumDetailDto.setDomains(domains);
        curriculumDetailDto.setSchools(schools);
        curriculumDetailDto.setDegrees(degrees);
    }

    private Map<Long, Set<Long>> collectDomainRequirements(CurriculumDetailDto detail) {
        CurriculumDto curriculum = detail.getCurriculum();
        UserDto user = detail.getUser();

        Stream<Map.Entry<Long, Long>> curriculumSkillsStream = Stream.empty();
        if (curriculum != null) {
            Stream<ProjectDomainOptionDto> personalSkills = Optional.ofNullable(curriculum.getDomainOptions())
                    .orElse(Set.of())
                    .stream();

            Stream<ProjectDomainOptionDto> projectSkills = Optional.ofNullable(curriculum.getProjects())
                    .orElse(Set.of())
                    .stream()
                    .map(project -> Optional.ofNullable(project.getDomainOptions()).orElse(Set.of()))
                    .flatMap(Collection::stream);

            curriculumSkillsStream = Stream.concat(personalSkills, projectSkills)
                    .filter(Objects::nonNull)
                    .map(dto -> Map.entry(dto.getDomainId(), dto.getDomainOptionId()));
        }

        Stream<Map.Entry<Long, Long>> userSkillsStream = Stream.empty();
        if (user != null) {
            userSkillsStream = Optional.ofNullable(user.getUserDomainOptions())
                    .orElse(Set.of())
                    .stream()
                    .filter(Objects::nonNull)
                    .map(dto -> Map.entry(dto.getDomainId(), dto.getDomainOptionId()));
        }

        return Stream.concat(curriculumSkillsStream, userSkillsStream)
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .collect(Collectors.groupingBy(
                        Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toSet())
                ));
    }
}