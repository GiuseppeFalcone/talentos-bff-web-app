package com.certimetergroup.easycv.bffwebapp.mapper;

import com.certimetergroup.easycv.bffwebapp.dto.curriculum.CurriculumAndUserLightDto;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumLightDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserLightDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CurriculumAndUserMapper {

    @Mapping(target = "userId", source = "user.userId")
    CurriculumAndUserLightDto toDto(UserLightDto user, CurriculumLightDto curriculum);
}
