package com.certimetergroup.talentos.bffwebapp.mapper;

import com.certimetergroup.talentos.bffwebapp.dto.view.youremployee.CurriculumAndUserLightDto;
import com.certimetergroup.talentos.commons.response.dto.curriculum.CurriculumLightDto;
import com.certimetergroup.talentos.commons.response.dto.user.UserLightDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CurriculumAndUserMapper {

    @Mapping(target = "userId", source = "user.userId")
    CurriculumAndUserLightDto toDto(UserLightDto user, CurriculumLightDto curriculum);
}
