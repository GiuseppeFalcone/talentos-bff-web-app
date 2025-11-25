package com.certimetergroup.easycv.bffwebapp.restclient;

import com.certimetergroup.easycv.bffwebapp.context.RequestContext;
import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.utility.RestHeaderHelper;
import com.certimetergroup.easycv.commons.response.Response;
import com.certimetergroup.easycv.commons.response.dto.domain.CreateDomainDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainOptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DomainApiClient {

    @Value("${domain-api.endpoint.get-all}")
    String getDomainsUrl;
    @Value("${domain-api.endpoint.get-by-id}")
    String getDomainByIdUrl;
    @Value("${domain-api.endpoint.post}")
    String postDomainUrl;
    @Value("${domain-api.endpoint.put}")
    String putDomainUrl;
    @Value("${domain-api.endpoint.delete}")
    String deleteDomainUrl;
    @Value("${domain-api.endpoint.option-by-id}")
    String getDomainOptionByIdUrl;

    private final RequestContext requestContext;
    private final RestTemplate restTemplateDomainApi;

    @Cacheable(value = "domains")
    public PagedResponseDto<DomainDto> getDomains(Integer page, Integer pageSize, String domainName, String domainOptionValue) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(getDomainsUrl)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize);
        if (domainName != null) {
            builder.queryParam("domainName", domainName);
        }
        if (domainOptionValue != null) {
            builder.queryParam("domainOptionValue", domainOptionValue);
        }

        ParameterizedTypeReference<Response<PagedResponseDto<DomainDto>>> responseType = new ParameterizedTypeReference<>() {};
        HttpEntity<Void> entity = new HttpEntity<>(RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken()));

        ResponseEntity<Response<PagedResponseDto<DomainDto>>> response = restTemplateDomainApi.exchange(
                builder.toUriString(), HttpMethod.GET, entity, responseType
        );
        return response.getBody().getData();
    }

    @Cacheable(value = "domain")
    public DomainDto getDomain(Long domainId, Set<Long> domainOptionIds) {
        ParameterizedTypeReference<Response<DomainDto>> responseType = new ParameterizedTypeReference<>() {};
        HttpEntity<Void> entity = new HttpEntity<>(RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken()));

        ResponseEntity<Response<DomainDto>> response = restTemplateDomainApi.exchange(
                getDomainByIdUrl, HttpMethod.GET, entity, responseType, Map.of("domainId", domainId, "domainOptionids", domainOptionIds)
        );
        return response.getBody().getData();
    }

    @CacheEvict(value = "domains", allEntries = true)
    public DomainDto addNewDomain(CreateDomainDto createDomainDto) {
        ParameterizedTypeReference<Response<DomainDto>> responseType = new ParameterizedTypeReference<>() {};
        HttpEntity<CreateDomainDto> entity = new HttpEntity<>(createDomainDto, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken()));

        ResponseEntity<Response<DomainDto>> response = restTemplateDomainApi.exchange(
                postDomainUrl, HttpMethod.POST, entity, responseType
        );
        return response.getBody().getData();
    }

    @Caching(evict = {
            @CacheEvict(value = "domains", allEntries = true),
            @CacheEvict(value = "domain", allEntries = true),
            @CacheEvict(value = "domainOption", allEntries = true)
    })
    public DomainDto replaceDomainData(Long domainId, DomainDto domainDto) {
        ParameterizedTypeReference<Response<DomainDto>> responseType = new ParameterizedTypeReference<>() {};
        HttpEntity<DomainDto> entity = new HttpEntity<>(domainDto, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken()));

        ResponseEntity<Response<DomainDto>> response = restTemplateDomainApi.exchange(
                putDomainUrl, HttpMethod.PUT, entity, responseType, Map.of("domainId", domainId)
        );
        return response.getBody().getData();
    }

    @Caching(evict = {
            @CacheEvict(value = "domains", allEntries = true),
            @CacheEvict(value = "domain", allEntries = true),
            @CacheEvict(value = "domainOption", allEntries = true)
    })
    public void deleteDomain(Long domainId) {
        ParameterizedTypeReference<Response<Void>> responseType = new ParameterizedTypeReference<>() {};
        HttpEntity<Void> entity = new HttpEntity<>(RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken()));

        restTemplateDomainApi.exchange(
                deleteDomainUrl, HttpMethod.DELETE, entity, responseType, Map.of("domainId", domainId)
        );
    }

    @Cacheable(value = "domainOption", key = "#domainOptionId")
    public DomainOptionDto getDomainOption(Long domainOptionId) {
        ParameterizedTypeReference<Response<DomainOptionDto>> responseType = new ParameterizedTypeReference<>() {};
        HttpEntity<Void> entity = new HttpEntity<>(RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken()));
        ResponseEntity<Response<DomainOptionDto>> response = restTemplateDomainApi.exchange(
                getDomainOptionByIdUrl, HttpMethod.GET, entity, responseType, Map.of("domainOptionId", domainOptionId)
        );
        return response.getBody().getData();
    }
}