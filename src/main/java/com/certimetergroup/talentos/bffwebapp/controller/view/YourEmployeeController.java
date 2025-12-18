package com.certimetergroup.talentos.bffwebapp.controller.view;

import com.certimetergroup.talentos.bffwebapp.context.RequestContext;
import com.certimetergroup.talentos.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.talentos.bffwebapp.dto.view.youremployee.CurriculumAndUserLightDto;
import com.certimetergroup.talentos.bffwebapp.service.AuthorizationService;
import com.certimetergroup.talentos.bffwebapp.service.rest.CurriculumApiService;
import com.certimetergroup.talentos.bffwebapp.service.rest.UserApiService;
import com.certimetergroup.talentos.bffwebapp.service.views.YourEmployeeService;
import com.certimetergroup.talentos.commons.enumeration.ResponseEnum;
import com.certimetergroup.talentos.commons.enumeration.UserRoleEnum;
import com.certimetergroup.talentos.commons.response.Response;
import com.certimetergroup.talentos.commons.response.dto.curriculum.CurriculumLightDto;
import com.certimetergroup.talentos.commons.response.dto.user.UserLightDto;
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
    private final RequestContext requestContext;

    @GetMapping
    public ResponseEntity<Response<PagedResponseDto<CurriculumAndUserLightDto>>> getUsersAndCurriculums(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String searchString,
            @RequestParam(required = false) UserRoleEnum queryRole,
            @RequestParam(required = false) Set<Long> domainOptionIds,
            @RequestParam(required = false) Set<Long> queryUserIds
    ) {
        authorizationService.checkGetUsers(queryUserIds, null);

        if (queryUserIds == null || !queryUserIds.isEmpty())
            queryUserIds = requestContext.getUser().getEmployeeIds();

        PagedResponseDto<UserLightDto> userResponseDto = userApiService.getUsers(page, pageSize, searchString, queryRole, domainOptionIds, queryUserIds, null);

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
