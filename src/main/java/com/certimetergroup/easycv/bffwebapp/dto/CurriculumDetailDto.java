package com.certimetergroup.easycv.bffwebapp.dto;

import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainDto;
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
    private CurriculumDto curriculum;
    private Set<DomainDto> domains;
}