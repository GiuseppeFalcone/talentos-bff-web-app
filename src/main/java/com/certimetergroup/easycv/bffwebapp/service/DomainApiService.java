package com.certimetergroup.easycv.bffwebapp.service;

import com.certimetergroup.easycv.bffwebapp.dto.curriculum.CurriculumDetailDto;
import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.restclient.DomainApiClient;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.exception.FailureException;
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

    public Optional<DomainDto> getDomain(Long domainId, Set<Long> domainOptionIds) {
        return domainApiClient.getDomain(domainId, domainOptionIds);
    }

    public DomainDto addNewDomain(CreateDomainDto createDomainDto) {
        return domainApiClient.addNewDomain(createDomainDto);
    }

    public Optional<DomainDto> replaceDomainData(Long domainId, DomainDto domainDto) {
        return domainApiClient.replaceDomainData(domainId, domainDto);
    }

    public void deleteDomain(Long domainId) {
        domainApiClient.deleteDomain(domainId);
    }

    public Optional<DomainOptionDto> getDomainOption(Long domainOptionId){ return domainApiClient.getDomainOption(domainOptionId); }

    public void getAllCurriculumDomains(CurriculumDetailDto curriculumDetailDto) {
        Map<Long, Set<Long>> domainRequirements = collectDomainRequirements(curriculumDetailDto.getCurriculum());

        Set<DomainDto> domains = domainRequirements.entrySet().stream()
                .map(entry -> {
                    Long domainId = entry.getKey();
                    Set<Long> optionIds = entry.getValue();
                    return domainApiClient.getDomain(domainId, optionIds);
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        Set<DomainOptionDto> schools = new HashSet<>();
        Set<DomainOptionDto> degrees = new HashSet<>();
        curriculumDetailDto.getCurriculum().getEducationHistory().stream().forEach( entry -> {
            Long schoolId = entry.getSchoolNameId();
            Optional<DomainOptionDto> optionalSchool = domainApiClient.getDomainOption(schoolId);
            if (optionalSchool.isEmpty())
                throw new FailureException(ResponseEnum.NOT_FOUND);
            schools.add(optionalSchool.get());
            Long degreeId = entry.getDegreeNameId();
            Optional<DomainOptionDto> optionalDegree = domainApiClient.getDomainOption(degreeId);
            if (optionalDegree.isEmpty())
                throw new FailureException(ResponseEnum.NOT_FOUND);
            degrees.add(optionalDegree.get());
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