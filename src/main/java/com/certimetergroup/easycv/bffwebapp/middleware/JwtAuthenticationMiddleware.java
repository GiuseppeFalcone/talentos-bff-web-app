package com.certimetergroup.easycv.bffwebapp.middleware;

import com.certimetergroup.easycv.bffwebapp.context.RequestContext;
import com.certimetergroup.easycv.bffwebapp.service.JwtService;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.enumeration.UserRoleEnum;
import com.certimetergroup.easycv.commons.exception.FailureException;
import com.certimetergroup.easycv.commons.utility.HttpHeaderUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Set;

@Order(1)
@Component
@RequiredArgsConstructor
public class JwtAuthenticationMiddleware extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final RequestContext requestContext;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final Set<String> excludedPaths = Set.of("/api/bff-web-app/auth", "/api/bff-web-app/docs");

    @Value("${allowed-cors-origin}")
    private String allowedCorsOrigin;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
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
        } catch (FailureException exception) {
            response.setHeader("Access-Control-Allow-Origin", allowedCorsOrigin);
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization, Access-Control-Allow-Origin");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            handlerExceptionResolver.resolveException(request, response, null, exception);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (request.getMethod().equalsIgnoreCase("OPTIONS"))
            return true;

        String path = request.getRequestURI();
        return excludedPaths.stream().anyMatch(path::startsWith);
    }
}
