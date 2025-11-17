package com.certimetergroup.easycv.bffwebapp.controller;

import com.certimetergroup.easycv.bffwebapp.context.RequestContext;
import com.certimetergroup.easycv.bffwebapp.service.AuthenticationService;
import com.certimetergroup.easycv.bffwebapp.service.JwtService;
import com.certimetergroup.easycv.bffwebapp.service.UserApiService;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.response.Response;
import com.certimetergroup.easycv.commons.response.authentication.AccAndRefresh;
import com.certimetergroup.easycv.commons.response.authentication.Credential;
import com.certimetergroup.easycv.commons.response.authentication.LoginResponse;
import com.certimetergroup.easycv.commons.response.authentication.RefreshToken;
import com.certimetergroup.easycv.commons.response.dto.user.UserLightDto;
import com.certimetergroup.easycv.commons.utility.HttpHeaderUtil;
import io.jsonwebtoken.Claims;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/bff-web-app/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserApiService userApiService;
    private final JwtService jwtService;
    private final RequestContext requestContext;

    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> handleLogin(@RequestBody Credential credential) {
        UserLightDto userLightDto = authenticationService.authenticateUserByCredential(credential);
        String oldRefreshToken = userLightDto.getRefreshToken();

        String[] tokens = jwtService.generateLoginTokens(userLightDto);

        requestContext.setAccessToken(tokens[0]);
        if (oldRefreshToken == null || !oldRefreshToken.equals(tokens[1]))
            userApiService.patchUserData(userLightDto);

        return ResponseEntity.ok().body(
                new Response<>(ResponseEnum.SUCCESS,
                        LoginResponse.builder()
                                .userLightDto(userLightDto)
                                .accessToken(tokens[0])
                                .refreshToken(tokens[1])
                                .build()
                ));
    }

    @PostMapping("/login/refresh")
    public ResponseEntity<Response<AccAndRefresh>> handleLoginRefresh(
            @RequestBody RefreshToken refreshTokenReq,
            @RequestHeader(value = "Authorization") @NotBlank(message = "Access token in header required") String accessToken) {
        Long userId = jwtService.getClaimFromAccessToken(HttpHeaderUtil.sanitizeAccessToken(accessToken), Claims.SUBJECT, Long.class);

        UserLightDto userLightDto = userApiService.getRefreshTokenByUserId(userId);
        String refreshTokenDb = userLightDto.getRefreshToken();
        String refreshTokenRq = refreshTokenReq.getRefreshToken();

        if (!refreshTokenRq.equals(refreshTokenDb))
            return ResponseEntity.status(ResponseEnum.UNAUTHORIZED.httpStatus).body(new Response<>(ResponseEnum.UNAUTHORIZED));

        AccAndRefresh accessAndRefreshTokenPayload = jwtService.refreshTokens(accessToken, refreshTokenRq, userLightDto);
        requestContext.setAccessToken(accessAndRefreshTokenPayload.getAccessToken());
        if (!accessAndRefreshTokenPayload.getRefreshToken().equals(refreshTokenRq))
            userApiService.patchUserData(userLightDto);

        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, accessAndRefreshTokenPayload));
    }

    @PostMapping("/reset")
    public ResponseEntity<Response<Void>> resetPassword(
            @RequestBody Credential credential) {
        userApiService.patchResetPassword(credential);
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS));
    }
}
