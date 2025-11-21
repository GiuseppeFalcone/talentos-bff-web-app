package com.certimetergroup.easycv.bffwebapp.controller;

import com.certimetergroup.easycv.bffwebapp.service.UserApiService;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.response.Response;
import com.certimetergroup.easycv.commons.response.dto.user.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/bff-web-app/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserApiService userApiService;

    @GetMapping("/{userId}")
    public ResponseEntity<Response<UserDto>> getUser(
            @PathVariable @NotNull(message = "userId as path variable required") Long userId
    ) {
        Optional<UserDto> optionalUserDto = userApiService.getUserById(userId);
        return optionalUserDto.map( dto -> ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, dto)))
                .orElseGet(() -> ResponseEntity.status(ResponseEnum.NOT_FOUND.httpStatus).body(new Response<>(ResponseEnum.NOT_FOUND)));
    }

}
