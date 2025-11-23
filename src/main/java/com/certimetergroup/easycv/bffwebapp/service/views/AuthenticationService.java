package com.certimetergroup.easycv.bffwebapp.service.views;

import com.certimetergroup.easycv.bffwebapp.service.rest.UserApiService;
import com.certimetergroup.easycv.commons.response.authentication.Credential;
import com.certimetergroup.easycv.commons.response.dto.user.UserLightDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

@Service
@RequestScope
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserApiService userApiService;

    public UserLightDto authenticateUserByCredential(Credential credential) {
        return userApiService.getUserLightByCredential(credential);
    }
}
