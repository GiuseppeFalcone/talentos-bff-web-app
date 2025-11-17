package com.certimetergroup.easycv.bffwebapp.service;

import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.restclient.DomainApiClient;
import com.certimetergroup.easycv.commons.response.dto.domain.CreateDomainDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

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
}