package com.certimetergroup.easycv.bffwebapp.controller.view;

import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.dto.view.youremployee.CurriculumAndUserLightDto;
import com.certimetergroup.easycv.bffwebapp.service.AuthorizationService;
import com.certimetergroup.easycv.bffwebapp.service.rest.CurriculumApiService;
import com.certimetergroup.easycv.bffwebapp.service.rest.UserApiService;
import com.certimetergroup.easycv.bffwebapp.service.views.YourEmployeeService;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.enumeration.UserRoleEnum;
import com.certimetergroup.easycv.commons.response.Response;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumLightDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserLightDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/bff-web-app/views/your-employees")
@RequiredArgsConstructor
@Tag(name = "BFF Your Employees", description = "Endpoints to serve your-employees front-end component")
public class YourEmployeeController {
    private final UserApiService userApiService;
    private final CurriculumApiService curriculumApiService;
    private final YourEmployeeService yourEmployeeService;
    private final AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<Response<PagedResponseDto<CurriculumAndUserLightDto>>> getUsersAndCurriculums(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false) UserRoleEnum queryRole,
            @RequestParam(required = false) Set<Long> domainOptionIds
    ) {

        authorizationService.checkGetUsers();

        PagedResponseDto<UserLightDto> userResponseDto = userApiService.getUsers(page, pageSize, searchString, queryRole, domainOptionIds);

        Set<Long> fetchedUserIds = userResponseDto.getContent().stream()
                .map(UserLightDto::getUserId)
                .collect(Collectors.toSet());

        if (fetchedUserIds.isEmpty())
            return ResponseEntity.status(ResponseEnum.NOT_FOUND.httpStatus).body(new Response<>(ResponseEnum.NOT_FOUND));

        authorizationService.checkGetCurriculums(fetchedUserIds);

        PagedResponseDto<CurriculumLightDto> curriculumResponseDto = curriculumApiService.getCurriculums(
                page,
                fetchedUserIds.size(),
                fetchedUserIds,
                domainOptionIds
        );

        PagedResponseDto<CurriculumAndUserLightDto> result = yourEmployeeService.curriculumAndUserLightDtoPagedResponseDto(
                userResponseDto,
                curriculumResponseDto
        );

        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, result));
    }
}
