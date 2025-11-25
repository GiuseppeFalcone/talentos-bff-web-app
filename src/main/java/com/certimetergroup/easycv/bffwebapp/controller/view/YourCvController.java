package com.certimetergroup.easycv.bffwebapp.controller.view;

import com.certimetergroup.easycv.bffwebapp.context.RequestContext;
import com.certimetergroup.easycv.bffwebapp.dto.view.yourcv.CreateCurriculumDetailDto;
import com.certimetergroup.easycv.bffwebapp.dto.view.yourcv.CurriculumDetailDto;
import com.certimetergroup.easycv.bffwebapp.dto.view.yourcv.UpdateCurriculumDto;
import com.certimetergroup.easycv.bffwebapp.service.AuthorizationService;
import com.certimetergroup.easycv.bffwebapp.service.rest.CurriculumApiService;
import com.certimetergroup.easycv.bffwebapp.service.rest.DomainApiService;
import com.certimetergroup.easycv.bffwebapp.service.rest.UserApiService;
import com.certimetergroup.easycv.bffwebapp.service.views.YourCvService;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.exception.FailureException;
import com.certimetergroup.easycv.commons.response.Response;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.ProjectDomainOptionDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.create.CreateCurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserDomainOptionDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/bff-web-app/views/your-cv")
@RequiredArgsConstructor
@Tag(name = "BFF Your Cv", description = "Endpoints to serve your-cv front-end component")
public class YourCvController {
    private final AuthorizationService authorizationService;
    private final CurriculumApiService curriculumApiService;
    private final UserApiService userApiService;
    private final DomainApiService domainApiService;
    private final YourCvService yourCvService;
    private final RequestContext requestContext;

    @GetMapping("/{curriculumId}")
    public ResponseEntity<Response<CurriculumDetailDto>> getCurriculumDetails(
            @PathVariable @NotNull(message = "Curriculum Id required") @Positive(message = "Curriculum Id must be > 0") Long curriculumId) {

        authorizationService.checkReadCurriculum(curriculumId);

        CurriculumDto curriculumDto = curriculumApiService.getCurriculum(curriculumId);
        UserDto userDto = userApiService.getUserById(curriculumDto.getUserId());

        CurriculumDetailDto curriculumDetailDto = new CurriculumDetailDto();
        curriculumDetailDto.setUser(userDto);
        curriculumDetailDto.setCurriculum(curriculumDto);
        domainApiService.getAllCurriculumDomains(curriculumDetailDto);

        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, curriculumDetailDto));
    }

    @PostMapping
    public ResponseEntity<Response<CurriculumDetailDto>> createNewCurriculum(
            @RequestBody @NotNull(message = "CreateCurriculumDetailDto required") CreateCurriculumDetailDto createCurriculumDetailDto) {

        Long userId = requestContext.getUserId();
        UserDto userDto = createCurriculumDetailDto.getUserDto();

        CurriculumDto createdCurriculum = null;
        try {
            createdCurriculum = curriculumApiService.addNewCurriculum(createCurriculumDetailDto.getCreateCurriculumDto());

            yourCvService.updateUserDomainOptions(createdCurriculum, userDto);

            userDto = userApiService.replaceUserData(userId, userDto);

            CurriculumDetailDto curriculumDetailDto = CurriculumDetailDto.builder()
                    .user(userDto)
                    .curriculum(createdCurriculum)
                    .build();

            domainApiService.getAllCurriculumDomains(curriculumDetailDto);

            return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, curriculumDetailDto));

        } catch (Exception exception) {
            if (createdCurriculum != null)
                curriculumApiService.deleteCurriculum(createdCurriculum.getCurriculumId());
            if (exception instanceof FailureException) {
                throw exception;
            }
            throw new FailureException(ResponseEnum.INTERNAL_SERVER_ERROR, exception);
        }
    }

    @PutMapping("/{curriculumId}")
    public ResponseEntity<Response<CurriculumDetailDto>> replaceCurriculumData(
            @PathVariable @NotNull(message = "Curriculum Id required") @Positive(message = "Wrong curriculum id provided") Long curriculumId,
            @RequestBody @NotNull(message = "UserDto required") UpdateCurriculumDto updateCurriculumDto) {

        authorizationService.checkWriteCurriculum(curriculumId);
        authorizationService.checkWriteUser(updateCurriculumDto.getUser().getUserId());

        UserDto oldUserDto = userApiService.getUserById(updateCurriculumDto.getUser().getUserId());
        UserDto updatedUserDto = oldUserDto;
        yourCvService.updateUserDomainOptions(updateCurriculumDto.getCurriculum(), updatedUserDto);
        updatedUserDto = userApiService.replaceUserData(updateCurriculumDto.getUser().getUserId(), updateCurriculumDto.getUser());

        CurriculumDto curriculumDto;
        try {
            curriculumDto = curriculumApiService.replaceCurriculumData(curriculumId, updateCurriculumDto.getCurriculum());
        } catch (FailureException failureException) {
            performUserRollback(oldUserDto);
            throw failureException;
        } catch (Exception e) {
            performUserRollback(oldUserDto);
            throw new FailureException(ResponseEnum.EXTERNAL_SERVER_ERROR, e);
        }

        CurriculumDetailDto curriculumDetailDto = new CurriculumDetailDto();
        curriculumDetailDto.setUser(updatedUserDto);
        curriculumDetailDto.setCurriculum(curriculumDto);
        domainApiService.getAllCurriculumDomains(curriculumDetailDto);

        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, curriculumDetailDto));
    }

    private void performUserRollback(UserDto oldUserDto) {
        try {
            UserDto rollbackResult = userApiService.replaceUserData(oldUserDto.getUserId(), oldUserDto);
            if (!rollbackResult.equals(oldUserDto))
                throw new FailureException(ResponseEnum.INTERNAL_SERVER_ERROR);
        } catch (Exception rollbackEx) {
            throw new FailureException(ResponseEnum.INTERNAL_SERVER_ERROR, rollbackEx);
        }
    }
}
