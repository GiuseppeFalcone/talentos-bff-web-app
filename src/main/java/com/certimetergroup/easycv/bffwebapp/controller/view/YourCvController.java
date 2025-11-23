package com.certimetergroup.easycv.bffwebapp.controller.view;

import com.certimetergroup.easycv.bffwebapp.dto.view.yourcv.CurriculumDetailDto;
import com.certimetergroup.easycv.bffwebapp.dto.view.yourcv.UpdateCurriculumDto;
import com.certimetergroup.easycv.bffwebapp.service.CurriculumApiService;
import com.certimetergroup.easycv.bffwebapp.service.DomainApiService;
import com.certimetergroup.easycv.bffwebapp.service.UserApiService;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.exception.FailureException;
import com.certimetergroup.easycv.commons.response.Response;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserDto;
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
    private final CurriculumApiService curriculumApiService;
    private final UserApiService userApiService;
    private final DomainApiService domainApiService;

    @GetMapping("/{curriculumId}")
    public ResponseEntity<Response<CurriculumDetailDto>> getCurriculumDetails(
            @PathVariable @NotNull(message = "Curriculum Id required") @Positive(message = "Curriculum Id must be > 0") Long curriculumId) {
        CurriculumDto curriculumDto = curriculumApiService.getCurriculum(curriculumId);
        UserDto userDto = userApiService.getUserById(curriculumDto.getUserId());

        CurriculumDetailDto curriculumDetailDto = new CurriculumDetailDto();
        curriculumDetailDto.setUser(userDto);
        curriculumDetailDto.setCurriculum(curriculumDto);
        domainApiService.getAllCurriculumDomains(curriculumDetailDto);

        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, curriculumDetailDto));
    }

    @PutMapping("/{curriculumId}")
    public ResponseEntity<Response<CurriculumDetailDto>> replaceCurriculumData(
            @PathVariable @NotNull(message = "Curriculum Id required") @Positive(message = "Wrong curriculum id provided") Long curriculumId,
            @RequestBody @NotNull(message = "UserDto required") UpdateCurriculumDto updateCurriculumDto) {

        UserDto oldUserDto = userApiService.getUserById(updateCurriculumDto.getUser().getUserId());
        UserDto updatedUserDto = userApiService.replaceUserData(updateCurriculumDto.getUser().getUserId(), updateCurriculumDto.getUser());

        CurriculumDto curriculumDto;
        try {
            curriculumDto = curriculumApiService.replaceCurriculumData(curriculumId, updateCurriculumDto.getCurriculum());
        } catch (Exception e) {
            performRollback(oldUserDto);
            if (e instanceof FailureException)
                throw e;
            throw new FailureException(ResponseEnum.EXTERNAL_SERVER_ERROR, e);
        }

        CurriculumDetailDto curriculumDetailDto = new CurriculumDetailDto();
        curriculumDetailDto.setUser(updatedUserDto);
        curriculumDetailDto.setCurriculum(curriculumDto);
        domainApiService.getAllCurriculumDomains(curriculumDetailDto);

        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, curriculumDetailDto));
    }

    private void performRollback(UserDto oldUserDto) {
        try {
            UserDto rollbackResult = userApiService.replaceUserData(oldUserDto.getUserId(), oldUserDto);
            if (!rollbackResult.equals(oldUserDto))
                throw new FailureException(ResponseEnum.INTERNAL_SERVER_ERROR);
        } catch (Exception rollbackEx) {
            throw new FailureException(ResponseEnum.INTERNAL_SERVER_ERROR, rollbackEx);
        }
    }
}
