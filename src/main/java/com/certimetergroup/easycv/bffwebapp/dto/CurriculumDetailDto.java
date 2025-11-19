package com.certimetergroup.easycv.bffwebapp.dto;

import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainOptionDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumDetailDto {
    private UserDto user;
    private CurriculumDto curriculum;
    private Set<DomainDto> domains;
    private Set<DomainOptionDto> schools;
    private Set<DomainOptionDto> degrees;
}