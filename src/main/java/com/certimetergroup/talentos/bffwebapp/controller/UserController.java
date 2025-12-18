package com.certimetergroup.talentos.bffwebapp.controller;

import com.certimetergroup.talentos.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.talentos.bffwebapp.service.AuthorizationService;
import com.certimetergroup.talentos.bffwebapp.service.rest.UserApiService;
import com.certimetergroup.talentos.commons.enumeration.ResponseEnum;
import com.certimetergroup.talentos.commons.enumeration.UserRoleEnum;
import com.certimetergroup.talentos.commons.response.Response;
import com.certimetergroup.talentos.commons.response.authentication.Credential;
import com.certimetergroup.talentos.commons.response.dto.user.CreateUserDto;
import com.certimetergroup.talentos.commons.response.dto.user.UserDto;
import com.certimetergroup.talentos.commons.response.dto.user.UserLightDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/bff-web-app/users")
@Validated
@RequiredArgsConstructor
@Tag(name = "BFF Users", description = "Endpoints to serve bff requests for user related data")
public class UserController {

    private final UserApiService userApiService;
    private final AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<Response<PagedResponseDto<UserLightDto>>> getUsers(
            @RequestParam(defaultValue = "1") @Positive(message = "Page must be >= 1") Integer page,
            @RequestParam(defaultValue = "10") @Positive(message = "Page size must be >= 0") Integer pageSize,
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false) UserRoleEnum queryRole,
            @RequestParam(required = false) Set<Long> domainOptionIds,
            @RequestParam(required = false) Set<Long> queryUserIds,
            @RequestParam(required = false) String matchUsername
    ) {

        authorizationService.checkGetUsers(queryUserIds, matchUsername);

        PagedResponseDto<UserLightDto> result = userApiService.getUsers(page, pageSize, searchString, queryRole, domainOptionIds, queryUserIds, matchUsername);

        if (matchUsername != null && !result.getContent().isEmpty()) {
            return ResponseEntity
                    .status(ResponseEnum.ALREADY_EXISTS.httpStatus)
                    .body(new Response<>(ResponseEnum.ALREADY_EXISTS));
        }

        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, result));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Response<UserDto>> getUser(@PathVariable @NotNull(message = "userId as path variable required") Long userId) {
        authorizationService.checkGetUser(userId);
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, userApiService.getUserById(userId)));
    }

    @PostMapping
    public ResponseEntity<Response<Credential>> createUser(@RequestBody CreateUserDto createUserDto) {
        authorizationService.checkCreateDeleteUser();
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, userApiService.createUser(createUserDto)));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Response<UserDto>> updateUser(
            @PathVariable @NotNull(message = "userId required") Long userId,
            @RequestBody UserDto userDto) {

        authorizationService.checkWriteUser(userId);
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, userApiService.replaceUserData(userId, userDto)));
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Response<UserDto>> patchUser(
            @PathVariable @NotNull(message = "userId required") Long userId,
            @RequestBody UserLightDto userLightDto) {

        authorizationService.checkWriteUser(userId);

        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, userApiService.patchUserData(userLightDto)));
    }

    @PatchMapping("/password")
    public ResponseEntity<Response<Void>> resetPassword(@RequestBody Credential credential) {
        authorizationService.checkCreateDeleteUser();
        userApiService.patchResetPassword(credential);
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Response<Void>> deleteUser(@PathVariable @NotNull(message = "userId required") Long userId) {
        authorizationService.checkCreateDeleteUser();
        userApiService.deleteUser(userId);
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS));
    }
}
