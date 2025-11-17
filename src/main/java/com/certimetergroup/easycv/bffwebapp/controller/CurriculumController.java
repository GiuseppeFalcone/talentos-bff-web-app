package com.certimetergroup.easycv.bffwebapp.controller;

import com.certimetergroup.easycv.bffwebapp.dto.CurriculumDetailDto;
import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.service.CurriculumApiService;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.response.Response;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumLightDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.create.CreateCurriculumDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
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

        Optional<CurriculumDto> optionalCurriculumDto = curriculumService.getCurriculum(curriculumId);
        return optionalCurriculumDto.map(
                        curriculumDto -> ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, curriculumDto)))
                .orElseGet(() -> ResponseEntity.status(ResponseEnum.NOT_FOUND.httpStatus).body(new Response<>(ResponseEnum.NOT_FOUND)));
    }

    @GetMapping("/{curriculumId}/details")
    public ResponseEntity<Response<CurriculumDetailDto>> getCurriculumDetails(
            @PathVariable @NotNull(message = "Curriculum Id required") @Positive(message = "Curriculum Id must be > 0") Long curriculumId) {

        Optional<CurriculumDetailDto> optionalDetailDto = curriculumService.getCurriculumDetails(curriculumId);
        return optionalDetailDto.map(
                        detailDto -> ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, detailDto)))
                .orElseGet(() -> ResponseEntity.status(ResponseEnum.NOT_FOUND.httpStatus).body(new Response<>(ResponseEnum.NOT_FOUND)));
    }

    @PostMapping
    public ResponseEntity<Response<CurriculumDto>> addNewCurriculum(
            @RequestBody @Valid @NotNull(message = "CreateCurriculumDto required") CreateCurriculumDto createCurriculumDto) {

        CurriculumDto newCurriculum = curriculumService.addNewCurriculum(createCurriculumDto);
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, newCurriculum));
    }

    @PutMapping("/{curriculumId}")
    public ResponseEntity<Response<CurriculumDto>> replaceCurriculumData(
            @PathVariable @NotNull(message = "Curriculum Id required") @Positive(message = "Wrong curriculum id provided") Long curriculumId,
            @RequestBody @Valid @NotNull(message = "CurriculumDto required") CurriculumDto curriculumDto) {

        Optional<CurriculumDto> optionalCurriculumDto = curriculumService.replaceCurriculumData(curriculumId, curriculumDto);
        return optionalCurriculumDto.map(
                        dto -> ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, dto)))
                .orElseGet(() -> ResponseEntity.status(ResponseEnum.NOT_FOUND.httpStatus).body(new Response<>(ResponseEnum.NOT_FOUND)));
    }

    @DeleteMapping("/{curriculumId}")
    public ResponseEntity<Response<Void>> deleteCurriculum(
            @PathVariable @NotNull(message = "Curriculum Id required") @Positive(message = "Wrong curriculum id provided") Long curriculumId) {

        curriculumService.deleteCurriculum(curriculumId);
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS));
    }
}