package com.certimetergroup.easycv.bffwebapp.controller;

import com.certimetergroup.easycv.bffwebapp.service.UserApiService;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.response.Response;
import com.certimetergroup.easycv.commons.response.dto.user.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bff-web-app/users")
@Validated
@RequiredArgsConstructor
@Tag(name = "BFF Users", description = "Endpoints to serve bff requests for user related data")
public class UserController {

    private final UserApiService userApiService;

    @GetMapping("/{userId}")
    public ResponseEntity<Response<UserDto>> getUser(@PathVariable @NotNull(message = "userId as path variable required") Long userId) {
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, userApiService.getUserById(userId)));
    }
}
