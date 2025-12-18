package com.certimetergroup.talentos.bffwebapp.dto.view.yourcv;

import com.certimetergroup.talentos.commons.response.dto.curriculum.create.CreateCurriculumDto;
import com.certimetergroup.talentos.commons.response.dto.user.UserDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateCurriculumDetailDto {
    @NotNull(message = "Create curriculum dto required")
    private CreateCurriculumDto createCurriculumDto;

    @NotNull(message = "User dto required")
    private UserDto userDto;
}
