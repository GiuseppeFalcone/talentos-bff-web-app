package com.certimetergroup.easycv.bffwebapp.dto.curriculum;

import com.certimetergroup.easycv.commons.enumeration.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurriculumAndUserLightDto {
    private Long curriculumId;
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private UserRoleEnum role;
    private Boolean hasCar;
    private Boolean openForTravel;
    private Integer numberOfProjects;
    private Boolean hasDegree;
}
