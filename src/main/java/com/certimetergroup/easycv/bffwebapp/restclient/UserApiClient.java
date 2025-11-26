package com.certimetergroup.easycv.bffwebapp.restclient;

import com.certimetergroup.easycv.bffwebapp.context.RequestContext;
import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.utility.RestHeaderHelper;
import com.certimetergroup.easycv.commons.enumeration.UserRoleEnum;
import com.certimetergroup.easycv.commons.response.Response;
import com.certimetergroup.easycv.commons.response.authentication.Credential;
import com.certimetergroup.easycv.commons.response.dto.user.CreateUserDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserLightDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserApiClient {
    @Value("${user-api.endpoint.baseurl}")
    String userApiBaseUrl;

    @Value("${user-api.endpoint.get-user-by-id}")
    String getUserByIdUrl;

    @Value("${user-api.endpoint.get-user-light-by-credential}")
    String getUserLightByCredentialUrl;

    @Value("${user-api.endpoint.get-user-light-by-id}")
    String getUserLightByIdUrl;

    @Value("${user-api.endpoint.patch}")
    String patchUserUrl;

    @Value("${user-api.endpoint.put}")
    String putUserUrl;

    @Value("${user-api.endpoint.reset-password}")
    String resetPasswordUrl;

    private final RequestContext requestContext;
    private final RestTemplate restTemplateUserApi;

    public PagedResponseDto<UserLightDto> getUsers(Integer page, Integer pageSize, String searchString,
                                                   UserRoleEnum queryRole, Set<Long> domainOptionIds,
                                                   Set<Long> queryUserIds, String matchUsername) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(userApiBaseUrl)
                .queryParam("page", page)
                .queryParam("pageSize", pageSize);

        if (searchString != null)
            builder.queryParam("searchString", searchString);

        if (queryRole != null)
            builder.queryParam("queryRole", queryRole);

        if (domainOptionIds != null && !domainOptionIds.isEmpty())
            builder.queryParam("domainOptionIds", domainOptionIds);

        if (queryUserIds != null && !queryUserIds.isEmpty())
            builder.queryParam("queryUserIds", queryUserIds);

        if (matchUsername != null && !matchUsername.isBlank())
            builder.queryParam("matchUsername", matchUsername);

        URI uri = builder.build().toUri();

        ParameterizedTypeReference<Response<PagedResponseDto<UserLightDto>>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Response<PagedResponseDto<UserLightDto>>> response = restTemplateUserApi.exchange(
                uri,
                HttpMethod.GET,
                new HttpEntity<>(RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType
        );
        return response.getBody().getData();
    }

    public UserLightDto getUserLoginByCredential(Credential credential) {
        ParameterizedTypeReference<Response<UserLightDto>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Response<UserLightDto>> response = restTemplateUserApi.exchange(
                getUserLightByCredentialUrl,
                HttpMethod.POST,
                new HttpEntity<>(credential, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType
        );
        return response.getBody().getData();
    }

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#userLightDto.userId"),
            @CacheEvict(value = "userLight", key = "#userLightDto.userId")
    })
    public UserDto patchUserData(UserLightDto userLightDto) {
        ParameterizedTypeReference<Response<UserDto>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Response<UserDto>> response = restTemplateUserApi.exchange(
                patchUserUrl,
                HttpMethod.PATCH,
                new HttpEntity<>(userLightDto, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType,
                Map.of("userId", userLightDto.getUserId())
        );

        return response.getBody().getData();
    }

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#userId"),
            @CacheEvict(value = "userLight", key = "#userId")
    })
    public UserDto replaceUserData(Long userId, UserDto userDto) {
        ParameterizedTypeReference<Response<UserDto>> responseType = new ParameterizedTypeReference<Response<UserDto>>() {};
        ResponseEntity<Response<UserDto>> response = restTemplateUserApi.exchange(
                putUserUrl,
                HttpMethod.PUT,
                new HttpEntity<>(userDto, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType,
                Map.of("userId", userId)
        );
        return response.getBody().getData();
    }

    @Cacheable(value = "userLight", key = "#userId")
    public UserLightDto getUserLightById(Long userId) {
        ParameterizedTypeReference<Response<UserLightDto>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Response<UserLightDto>> response = restTemplateUserApi.exchange(
                getUserLightByIdUrl,
                HttpMethod.GET,
                new HttpEntity<>(null, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType,
                Map.of("userId", userId)
        );
        return response.getBody().getData();
    }

    public void patchResetPassword(Credential credential) {
        ParameterizedTypeReference<Response<Void>> responseType = new ParameterizedTypeReference<>() {};
        restTemplateUserApi.exchange(
                resetPasswordUrl,
                HttpMethod.PATCH,
                new HttpEntity<>(credential, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType
        );
    }

    @Cacheable(value = "user", key = "#userId")
    public UserDto getUserById(Long userId) {
        ParameterizedTypeReference<Response<UserDto>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Response<UserDto>> response = restTemplateUserApi.exchange(
                getUserByIdUrl,
                HttpMethod.GET,
                new HttpEntity<>(null, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType,
                Map.of("userId", userId)
        );
        return response.getBody().getData();
    }

    @Caching(evict = {
            @CacheEvict(value = "user", key = "#userId"),
            @CacheEvict(value = "userLight", key = "#userId")
    })
    public void deleteUser(Long userId) {
        ParameterizedTypeReference<Response<Void>> responseType = new ParameterizedTypeReference<>() {};
        restTemplateUserApi.exchange(
                getUserByIdUrl,
                HttpMethod.DELETE,
                new HttpEntity<>(RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType,
                Map.of("userId", userId)
        );
    }

    public Credential createUser(CreateUserDto createUserDto) {
        ParameterizedTypeReference<Response<Credential>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Response<Credential>> response = restTemplateUserApi.exchange(
                userApiBaseUrl,
                HttpMethod.POST,
                new HttpEntity<>(createUserDto, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType
        );
        return response.getBody().getData();
    }
}