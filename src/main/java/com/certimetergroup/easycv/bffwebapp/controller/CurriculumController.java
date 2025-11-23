package com.certimetergroup.easycv.bffwebapp.controller;

import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.service.CurriculumApiService;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.response.Response;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumLightDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.create.CreateCurriculumDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/bff-web-app/curriculums")
@Tag(name = "BFF Curriculums", description = "BFF operations for curriculum data")
@Validated
@RequiredArgsConstructor
public class CurriculumController {
    private final CurriculumApiService curriculumService;

    @GetMapping
    public ResponseEntity<Response<PagedResponseDto<CurriculumLightDto>>> getCurriculums(
            @RequestParam(defaultValue = "1") @Positive(message = "Page must be > 0") Integer page,
            @RequestParam(defaultValue = "5") @Positive(message = "Page size must be > 0") Integer pageSize,
            @RequestParam(required = false) Set<Long> userIds,
            @RequestParam(required = false) Long domainId,
            @RequestParam(required = false) Long domainOptionId) {

        PagedResponseDto<CurriculumLightDto> pagedResponseDto = curriculumService.getCurriculums(page, pageSize, userIds, domainId, domainOptionId);
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, pagedResponseDto));
    }

    @GetMapping("/{curriculumId}")
    public ResponseEntity<Response<CurriculumDto>> getCurriculum(
            @PathVariable @NotNull(message = "Curriculum Id required") @Positive(message = "Curriculum Id must be > 0") Long curriculumId) {

        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, curriculumService.getCurriculum(curriculumId)));
    }

    @PostMapping
    public ResponseEntity<Response<CurriculumDto>> addNewCurriculum(
            @RequestBody @NotNull(message = "CreateCurriculumDto required") CreateCurriculumDto createCurriculumDto) {

        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, curriculumService.addNewCurriculum(createCurriculumDto)));
    }

    @DeleteMapping("/{curriculumId}")
    public ResponseEntity<Response<Void>> deleteCurriculum(
            @PathVariable @NotNull(message = "Curriculum Id required") @Positive(message = "Wrong curriculum id provided") Long curriculumId) {

        curriculumService.deleteCurriculum(curriculumId);
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS));
    }
}