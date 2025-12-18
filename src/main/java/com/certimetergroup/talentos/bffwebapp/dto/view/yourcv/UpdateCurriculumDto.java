package com.certimetergroup.talentos.bffwebapp.dto.view.yourcv;

import com.certimetergroup.talentos.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.talentos.commons.response.dto.user.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class UpdateCurriculumDto {
    @NotNull(message = "CurriculumDto required")
    private CurriculumDto curriculum;

    @NotNull(message = "UserDto required")
    private UserDto user;
}
