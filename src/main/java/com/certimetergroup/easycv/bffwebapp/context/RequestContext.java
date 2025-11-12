package com.certimetergroup.easycv.bffwebapp.context;

import com.certimetergroup.easycv.commons.enumeration.UserRoleEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Data
@NoArgsConstructor
public class RequestContext {
    private Long userId;
    private UserRoleEnum userRole;
    private String accessToken;
}
