package com.certimetergroup.easycv.bffwebapp.restclient;

import com.certimetergroup.easycv.bffwebapp.context.RequestContext;
import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.utility.RestHeaderHelper;
import com.certimetergroup.easycv.commons.enumeration.UserRoleEnum;
import com.certimetergroup.easycv.commons.response.Response;
import com.certimetergroup.easycv.commons.response.authentication.Credential;
import com.certimetergroup.easycv.commons.response.dto.user.UserDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserLightDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

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

    public PagedResponseDto<UserLightDto> getUsers(Integer page, Integer pageSize, String queryUsername, UserRoleEnum queryRole) {
        ParameterizedTypeReference<Response<PagedResponseDto<UserLightDto>>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Response<PagedResponseDto<UserLightDto>>> response = restTemplateUserApi.exchange(
                userApiBaseUrl,
                HttpMethod.GET,
                new HttpEntity<>(RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType,
                Map.of("page", page, "pageSize", pageSize, "queryUsername", queryUsername, "queryRole", queryRole)
        );
        return response.getBody().getData();
    }

    public Optional<UserLightDto> getUserLoginByCredential(Credential credential) {
        ParameterizedTypeReference<Response<UserLightDto>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Response<UserLightDto>> response = restTemplateUserApi.exchange(
                getUserLightByCredentialUrl,
                HttpMethod.POST,
                new HttpEntity<>(credential, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType
        );
        return Optional.ofNullable(response.getBody().getData());
    }

    public Optional<UserDto> patchUserData(UserLightDto userLightDto) {
        ParameterizedTypeReference<Response<UserDto>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Response<UserDto>> response = restTemplateUserApi.exchange(
                patchUserUrl,
                HttpMethod.PATCH,
                new HttpEntity<>(userLightDto, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType,
                Map.of("userId", userLightDto.getUserId())
        );

        return Optional.ofNullable(response.getBody().getData());
    }

    public Optional<UserDto> replaceUserData(Long userId, UserDto userDto) {
        ParameterizedTypeReference<Response<UserDto>> responseType = new ParameterizedTypeReference<Response<UserDto>>() {};
        ResponseEntity<Response<UserDto>> response = restTemplateUserApi.exchange(
                putUserUrl,
                HttpMethod.PUT,
                new HttpEntity<>(userDto, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType,
                Map.of("userId", userId)
        );
        return Optional.ofNullable(response.getBody().getData());
    }

    public Optional<UserLightDto> getUserLightById(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(requestContext.getAccessToken());
        ParameterizedTypeReference<Response<UserLightDto>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Response<UserLightDto>> response = restTemplateUserApi.exchange(
                getUserLightByIdUrl,
                HttpMethod.GET,
                new HttpEntity<>(null, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType,
                Map.of("userId", userId)
        );
        return Optional.ofNullable(response.getBody().getData());
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

    public Optional<UserDto> getUserById(Long userId) {
        ParameterizedTypeReference<Response<UserDto>> responseType = new ParameterizedTypeReference<>() {};
        ResponseEntity<Response<UserDto>> response = restTemplateUserApi.exchange(
                getUserByIdUrl,
                HttpMethod.GET,
                new HttpEntity<>(null, RestHeaderHelper.createAuthHeaders(requestContext.getAccessToken())),
                responseType,
                Map.of("userId", userId)
        );
        return Optional.ofNullable(response.getBody().getData());
    }
}
