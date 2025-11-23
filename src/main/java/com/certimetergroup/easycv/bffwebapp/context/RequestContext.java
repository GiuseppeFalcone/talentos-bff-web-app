package com.certimetergroup.easycv.bffwebapp.context;

import com.certimetergroup.easycv.commons.enumeration.UserRoleEnum;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserDto;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Data
@Component
@RequestScope
public class RequestContext {
    private Long userId;
    private UserRoleEnum userRole;
    private String accessToken;
    private UserDto user;
    private CurriculumDto userCurriculum;
}
