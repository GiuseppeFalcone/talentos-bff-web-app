package com.certimetergroup.easycv.bffwebapp.restclient;

import com.certimetergroup.easycv.bffwebapp.context.RequestContext;
import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.utility.RestHeaderHelper;
import com.certimetergroup.easycv.commons.response.Response;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumLightDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.create.CreateCurriculumDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CurriculumApiClient {

    @Value("${curriculum-api.endpoint.get-all}")
    String getCurriculumsUrl;
    @Value("${curriculum-api.endpoint.get-by-id}")
    String getCurriculumByIdUrl;
    @Value("${curriculum-api.endpoint.post}")
    String postCurriculumUrl;
    @Value("${curriculum-api.endpoint.put}")
    String putCurriculumUrl;
    @Value("${curriculum-api.endpoint.delete}")
    String deleteCurriculumUrl;

    private final RequestContext requestContext;
    private final RestTemplate restTemplateCurriculumApi;

    public PagedResponseDto<CurriculumLightDto> getCurriculums(Integer page, Integer pageSize, Set<Long> userIds, Set<Long> domainOptionIds) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath(getCurriculumsUrl)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize);

        if (userIds != null && !userIds.isEmpty()) {
            builder.queryParam("userIds", userIds.toArray());
        }

        if (domainOptionIds != null && !domainOptionIds.isEmpty()) {
            builder.queryParam("domainOptionIds", domainOptionIds);
        }

        ParameterizedTypeReference<Response<PagedResponseDto<CurriculumLightDto>>> responseType = new ParameterizedTypeReference<>() {};
        HttpEntity<Void> entity = new HttpEntity<>(RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken()));

        ResponseEntity<Response<PagedResponseDto<CurriculumLightDto>>> response = restTemplateCurriculumApi.exchange(
                builder.toUriString(), HttpMethod.GET, entity, responseType
        );
        return response.getBody().getData();
    }

    @Cacheable(value = "curriculum", key = "#curriculumId")
    public CurriculumDto getCurriculum(Long curriculumId) {
        ParameterizedTypeReference<Response<CurriculumDto>> responseType = new ParameterizedTypeReference<>() {};
        HttpEntity<Void> entity = new HttpEntity<>(RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken()));

        ResponseEntity<Response<CurriculumDto>> response = restTemplateCurriculumApi.exchange(
                getCurriculumByIdUrl, HttpMethod.GET, entity, responseType, Map.of("curriculumId", curriculumId)
        );
        return response.getBody().getData();
    }

    @CachePut(value = "curriculum", key = "#result.curriculumId")
    public CurriculumDto addNewCurriculum(CreateCurriculumDto createCurriculumDto) {
        ParameterizedTypeReference<Response<CurriculumDto>> responseType = new ParameterizedTypeReference<>() {};
        HttpEntity<CreateCurriculumDto> entity = new HttpEntity<>(createCurriculumDto, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken()));

        ResponseEntity<Response<CurriculumDto>> response = restTemplateCurriculumApi.exchange(
                postCurriculumUrl, HttpMethod.POST, entity, responseType
        );
        return response.getBody().getData();
    }

    @CacheEvict(value = "curriculum", key = "#curriculumId")
    public CurriculumDto replaceCurriculumData(Long curriculumId, CurriculumDto curriculumDto) {
        ParameterizedTypeReference<Response<CurriculumDto>> responseType = new ParameterizedTypeReference<>() {};
        HttpEntity<CurriculumDto> entity = new HttpEntity<>(curriculumDto, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken()));

        ResponseEntity<Response<CurriculumDto>> response = restTemplateCurriculumApi.exchange(
                putCurriculumUrl, HttpMethod.PUT, entity, responseType, Map.of("curriculumId", curriculumId)
        );
        return response.getBody().getData();
    }

    @CacheEvict(value = "curriculum", key = "#curriculumId")
    public void deleteCurriculum(Long curriculumId) {
        ParameterizedTypeReference<Response<Void>> responseType = new ParameterizedTypeReference<>() {};
        HttpEntity<Void> entity = new HttpEntity<>(RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken()));

        restTemplateCurriculumApi.exchange(
                deleteCurriculumUrl, HttpMethod.DELETE, entity, responseType, Map.of("curriculumId", curriculumId)
        );
    }
}