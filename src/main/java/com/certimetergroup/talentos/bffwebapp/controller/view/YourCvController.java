package com.certimetergroup.talentos.bffwebapp.controller.view;

import com.certimetergroup.talentos.bffwebapp.context.RequestContext;
import com.certimetergroup.talentos.bffwebapp.dto.view.yourcv.CreateCurriculumDetailDto;
import com.certimetergroup.talentos.bffwebapp.dto.view.yourcv.CurriculumDetailDto;
import com.certimetergroup.talentos.bffwebapp.dto.view.yourcv.UpdateCurriculumDto;
import com.certimetergroup.talentos.bffwebapp.service.AuthorizationService;
import com.certimetergroup.talentos.bffwebapp.service.rest.CurriculumApiService;
import com.certimetergroup.talentos.bffwebapp.service.rest.DomainApiService;
import com.certimetergroup.talentos.bffwebapp.service.rest.UserApiService;
import com.certimetergroup.talentos.bffwebapp.service.views.YourCvService;
import com.certimetergroup.talentos.commons.enumeration.ResponseEnum;
import com.certimetergroup.talentos.commons.exception.FailureException;
import com.certimetergroup.talentos.commons.response.Response;
import com.certimetergroup.talentos.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.talentos.commons.response.dto.user.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<Response<CurriculumDetailDto>> getCurriculumDetails(
            @RequestParam @NotNull(message = "Curriculum Id required") @Positive(message = "Curriculum Id must be > 0") Long curriculumId) {

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

    @PutMapping
    public ResponseEntity<Response<CurriculumDetailDto>> replaceCurriculumData(
            @RequestParam @NotNull(message = "Curriculum Id required") @Positive(message = "Wrong curriculum id provided") Long curriculumId,
            @RequestBody @NotNull(message = "UserDto required") UpdateCurriculumDto updateCurriculumDto) {

        authorizationService.checkWriteCurriculum(curriculumId);
        authorizationService.checkWriteUser(updateCurriculumDto.getUser().getUserId());

        UserDto userDtoToSave = userApiService.getUserById(updateCurriculumDto.getUser().getUserId());
        UserDto oldUserDto = userApiService.getUserById(updateCurriculumDto.getUser().getUserId());

        if (updateCurriculumDto.getUser().getUserDomainOptions() != null) {
            userDtoToSave.setUserDomainOptions(updateCurriculumDto.getUser().getUserDomainOptions());
        }

        yourCvService.updateUserDomainOptions(updateCurriculumDto.getCurriculum(), userDtoToSave);

        UserDto updatedUserDto = userApiService.replaceUserData(updateCurriculumDto.getUser().getUserId(), userDtoToSave);

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
