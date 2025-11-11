package com.certimetergroup.easycv.bffwebapp.service;

import com.certimetergroup.easycv.bffwebapp.restclient.UserApiClient;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.exception.FailureException;
import com.certimetergroup.easycv.commons.response.authentication.Credential;
import com.certimetergroup.easycv.commons.response.dto.user.UserDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserLightDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApiService {
    private final UserApiClient userApiClient;

    public UserLightDto getUserLightByCredential(Credential credential) {
        Optional<UserLightDto> userLightDto = userApiClient.getUserLoginByCredential(credential);
        if (userLightDto.isEmpty())
            throw new FailureException(ResponseEnum.UNAUTHORIZED);
        return userLightDto.get();
    }

    public Optional<UserDto> patchUserData(UserLightDto userLightDto) {
        return userApiClient.patchUserData(userLightDto);
    }

    public UserLightDto getRefreshTokenByUserId(Long userId) {
        Optional<UserLightDto> optionalUserLightDto = userApiClient.getUserLightById(userId);
        if (optionalUserLightDto.isEmpty())
            throw new FailureException(ResponseEnum.UNAUTHORIZED);
        return optionalUserLightDto.get();
    }

    public void patchResetPassword(Credential credential) {
        userApiClient.patchResetPassword(credential);
    }
}
