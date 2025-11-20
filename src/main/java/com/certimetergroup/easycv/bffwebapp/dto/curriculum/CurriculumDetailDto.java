package com.certimetergroup.easycv.bffwebapp.dto.curriculum;

import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainDto;
import com.certimetergroup.easycv.commons.response.dto.domain.DomainOptionDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserDto;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "cannot be null")
    private UserDto user;
    @NotNull(message = "cannot be null")
    private CurriculumDto curriculum;
    @NotNull(message = "cannot be null")
    private Set<DomainDto> domains;
    @NotNull(message = "cannot be null")
    private Set<DomainOptionDto> schools;
    @NotNull(message = "cannot be null")
    private Set<DomainOptionDto> degrees;
}