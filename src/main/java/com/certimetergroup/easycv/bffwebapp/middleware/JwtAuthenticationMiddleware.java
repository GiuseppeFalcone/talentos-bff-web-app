package com.certimetergroup.easycv.bffwebapp.middleware;

import com.certimetergroup.easycv.bffwebapp.context.RequestContext;
import com.certimetergroup.easycv.bffwebapp.service.JwtService;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.enumeration.UserRoleEnum;
import com.certimetergroup.easycv.commons.exception.FailureException;
import com.certimetergroup.easycv.commons.utility.HttpHeaderUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Set;

@Order(2)
@Component
@RequiredArgsConstructor
public class JwtAuthenticationMiddleware extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final RequestContext requestContext;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final Set<String> excludedPaths = Set.of("/api/bff-web-app/auth", "/api/bff-web-app/docs");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            String accessToken = HttpHeaderUtil.sanitizeAccessToken(request.getHeader("Authorization"));

            if (jwtService.isAccessTokenExpired(accessToken))
                throw new FailureException(ResponseEnum.JWT_EXPIRED);
            Long userId = Long.decode(jwtService.getClaimFromAccessToken(accessToken, Claims.SUBJECT, String.class));
            String role = jwtService.getClaimFromAccessToken(accessToken, "role", String.class);

            requestContext.setUserId(userId);
            requestContext.setUserRole(UserRoleEnum.valueOf(role));
            requestContext.setAccessToken(accessToken);

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return true;
        }

        String path = request.getRequestURI();
        return excludedPaths.stream().anyMatch(path::startsWith);
    }
}
