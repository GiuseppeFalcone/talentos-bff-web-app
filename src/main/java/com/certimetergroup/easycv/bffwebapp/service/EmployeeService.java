package com.certimetergroup.easycv.bffwebapp.service;

import com.certimetergroup.easycv.bffwebapp.dto.Page;
import com.certimetergroup.easycv.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.easycv.bffwebapp.dto.curriculum.CurriculumAndUserLightDto;
import com.certimetergroup.easycv.bffwebapp.mapper.CurriculumAndUserMapper;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumLightDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserLightDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    public final CurriculumAndUserMapper curriculumAndUserMapper;

    public PagedResponseDto<CurriculumAndUserLightDto> curriculumAndUserLightDtoPagedResponseDto(
            PagedResponseDto<UserLightDto> userLightDtoPagedResponseDto,
            PagedResponseDto<CurriculumLightDto> curriculumLightDtoPagedResponseDto
    ) {
        List<UserLightDto> users = userLightDtoPagedResponseDto.getContent();
        List<CurriculumLightDto> curriculums = curriculumLightDtoPagedResponseDto.getContent();

        Map<Long, CurriculumLightDto> curriculumMap = curriculums.stream()
                .collect(Collectors.toMap(CurriculumLightDto::getUserId, Function.identity(), (a, b) -> a));

        List<CurriculumAndUserLightDto> content = users.stream().map(user -> {
            CurriculumLightDto curriculum = curriculumMap.get(user.getUserId());
            return curriculumAndUserMapper.toDto(user, curriculum);
        }).toList();

        Page page = Page.builder()
                .totalPages(userLightDtoPagedResponseDto.getPage().getTotalPages())
                .totalElements(userLightDtoPagedResponseDto.getPage().getTotalElements())
                .size(userLightDtoPagedResponseDto.getPage().getSize())
                .number(userLightDtoPagedResponseDto.getPage().getNumber())
                .build();
        
        return new PagedResponseDto<>(content, page);
    }
}
