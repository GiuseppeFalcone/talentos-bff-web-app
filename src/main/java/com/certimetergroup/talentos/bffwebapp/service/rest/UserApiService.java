package com.certimetergroup.talentos.bffwebapp.service.rest;

import com.certimetergroup.talentos.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.talentos.bffwebapp.restclient.UserApiClient;
import com.certimetergroup.talentos.commons.enumeration.UserRoleEnum;
import com.certimetergroup.talentos.commons.response.authentication.Credential;
import com.certimetergroup.talentos.commons.response.dto.user.CreateUserDto;
import com.certimetergroup.talentos.commons.response.dto.user.UserDto;
import com.certimetergroup.talentos.commons.response.dto.user.UserLightDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserApiService {
    private final UserApiClient userApiClient;

    public PagedResponseDto<UserLightDto> getUsers(Integer page, Integer pageSize, String query, UserRoleEnum queryRole,
                                                   Set<Long> domainOptionIds, Set<Long> queryUserIds, String matchUsername
    ) {
        return userApiClient.getUsers(page, pageSize, query, queryRole, domainOptionIds, queryUserIds, matchUsername);
    }

    public UserLightDto getUserLightByCredential(Credential credential) {
        return userApiClient.getUserLoginByCredential(credential);
    }

    public UserDto getUserById(Long userId) {
        return userApiClient.getUserById(userId);
    }

    public UserDto patchUserData(UserLightDto userLightDto) {
        return userApiClient.patchUserData(userLightDto);
    }

    public UserDto replaceUserData(Long userId, UserDto userDto) {
        return userApiClient.replaceUserData(userId, userDto);
    }

    public UserLightDto getRefreshTokenByUserId(Long userId) {
        return userApiClient.getUserLightById(userId);
    }

    public void patchResetPassword(Credential credential) {
        userApiClient.patchResetPassword(credential);
    }

    public Credential createUser(CreateUserDto createUserDto) {
        return userApiClient.createUser(createUserDto);
    }

    public void deleteUser(Long userId) {
        userApiClient.deleteUser(userId);
    }
}
