package com.certimetergroup.easycv.bffwebapp.service;

import com.certimetergroup.easycv.bffwebapp.context.RequestContext;
import com.certimetergroup.easycv.bffwebapp.service.rest.CurriculumApiService;
import com.certimetergroup.easycv.bffwebapp.service.rest.UserApiService;
import com.certimetergroup.easycv.commons.enumeration.ResponseEnum;
import com.certimetergroup.easycv.commons.enumeration.UserRoleEnum;
import com.certimetergroup.easycv.commons.exception.FailureException;
import com.certimetergroup.easycv.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.easycv.commons.response.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthorizationService {
    private final RequestContext requestContext;
    private final UserApiService userApiService;
    private final CurriculumApiService curriculumApiService;

    public void checkGetUsers() {
        UserRoleEnum role = requestContext.getUserRole();
        if (UserRoleEnum.EMPLOYEE.equals(role)) {
            throw new FailureException(ResponseEnum.UNAUTHORIZED);
        }
    }

    public void checkGetUser(Long targetUserId) {
        if (isAdminOrSuperAdmin()) return;

        Long currentUserId = requestContext.getUserId();

        if (currentUserId.equals(targetUserId)) return;

        if (UserRoleEnum.MANAGER.equals(requestContext.getUserRole())) {
            UserDto managerDto = fetchAndCacheUser(currentUserId);
            if (managerDto.getEmployeeIds() != null && managerDto.getEmployeeIds().contains(targetUserId)) {
                return;
            }
        }

        throw new FailureException(ResponseEnum.UNAUTHORIZED);
    }

    public void checkCreateDeleteUser() {
        if (!isAdminOrSuperAdmin()) {
            throw new FailureException(ResponseEnum.UNAUTHORIZED);
        }
    }

    public void checkWriteUser(Long targetUserId) {
        if (isAdminOrSuperAdmin()) return;

        if (!requestContext.getUserId().equals(targetUserId)) {
            throw new FailureException(ResponseEnum.UNAUTHORIZED);
        }
    }

    public void checkGetCurriculums(Set<Long> requestedUserIds) {
        if (isAdminOrSuperAdmin()) return;

        Long currentUserId = requestContext.getUserId();
        UserRoleEnum role = requestContext.getUserRole();

        if (requestedUserIds != null && !requestedUserIds.isEmpty()) {
            if (UserRoleEnum.EMPLOYEE.equals(role)) {
                if (requestedUserIds.size() > 1 || !requestedUserIds.contains(currentUserId)) {
                    throw new FailureException(ResponseEnum.UNAUTHORIZED);
                }
            } else if (UserRoleEnum.MANAGER.equals(role)) {
                UserDto manager = fetchAndCacheUser(currentUserId);

                Set<Long> allowedIds = new HashSet<>();
                allowedIds.add(currentUserId);
                if (manager.getEmployeeIds() != null) {
                    allowedIds.addAll(manager.getEmployeeIds());
                }

                if (!allowedIds.containsAll(requestedUserIds)) {
                    throw new FailureException(ResponseEnum.UNAUTHORIZED);
                }
            }
        }
    }

    public void checkReadCurriculum(Long curriculumId) {
        if (isAdminOrSuperAdmin()) return;

        CurriculumDto curriculum = fetchAndCacheCurriculum(curriculumId);
        Long ownerId = curriculum.getUserId();
        Long currentUserId = requestContext.getUserId();

        if (ownerId.equals(currentUserId)) return;

        if (UserRoleEnum.MANAGER.equals(requestContext.getUserRole())) {
            UserDto managerDto = fetchAndCacheUser(currentUserId);
            if (managerDto.getEmployeeIds() != null && managerDto.getEmployeeIds().contains(ownerId)) {
                return;
            }
        }

        throw new FailureException(ResponseEnum.UNAUTHORIZED);
    }

    public void checkWriteCurriculum(Long curriculumId) {
        if (isAdminOrSuperAdmin()) return;

        CurriculumDto curriculum = fetchAndCacheCurriculum(curriculumId);
        Long ownerId = curriculum.getUserId();

        if (!ownerId.equals(requestContext.getUserId())) {
            throw new FailureException(ResponseEnum.UNAUTHORIZED);
        }
    }

    public void checkWriteDomain() {
        if (!isAdminOrSuperAdmin()) {
            throw new FailureException(ResponseEnum.UNAUTHORIZED);
        }
    }

    private boolean isAdminOrSuperAdmin() {
        UserRoleEnum role = requestContext.getUserRole();
        return role == UserRoleEnum.ADMIN || role == UserRoleEnum.SUPERADMIN;
    }

    private UserDto fetchAndCacheUser(Long userId) {
        if (requestContext.getUser() != null && requestContext.getUser().getUserId().equals(userId)) {
            return requestContext.getUser();
        }
        UserDto user = userApiService.getUserById(userId);
        requestContext.setUser(user);
        return user;
    }

    private CurriculumDto fetchAndCacheCurriculum(Long curriculumId) {
        if (requestContext.getUserCurriculum() != null && requestContext.getUserCurriculum().getCurriculumId().equals(curriculumId)) {
            return requestContext.getUserCurriculum();
        }
        CurriculumDto curriculum = curriculumApiService.getCurriculum(curriculumId);
        requestContext.setUserCurriculum(curriculum);
        return curriculum;
    }
}