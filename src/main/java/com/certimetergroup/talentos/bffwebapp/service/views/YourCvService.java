package com.certimetergroup.talentos.bffwebapp.service.views;

import com.certimetergroup.talentos.commons.response.dto.curriculum.CurriculumDto;
import com.certimetergroup.talentos.commons.response.dto.curriculum.ProjectDomainOptionDto;
import com.certimetergroup.talentos.commons.response.dto.user.UserDomainOptionDto;
import com.certimetergroup.talentos.commons.response.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class YourCvService {

    public void updateUserDomainOptions(CurriculumDto curriculumDto, UserDto userDto) {
        Set<UserDomainOptionDto> userDomainOptions = userDto.getUserDomainOptions();
        if (userDomainOptions == null) {
            userDomainOptions = new HashSet<>();
            userDto.setUserDomainOptions(userDomainOptions);
        }

        Map<Long, UserDomainOptionDto> existingSkillsMap = userDomainOptions.stream()
                .collect(Collectors.toMap(UserDomainOptionDto::getDomainOptionId, Function.identity()));

        if (curriculumDto.getDomainOptions() != null) {
            for (ProjectDomainOptionDto cvSkill : curriculumDto.getDomainOptions()) {
                if (existingSkillsMap.containsKey(cvSkill.getDomainOptionId())) {
                    UserDomainOptionDto existing = existingSkillsMap.get(cvSkill.getDomainOptionId());
                    if (existing.getGrade() < cvSkill.getGrade()) {
                        existing.setGrade(cvSkill.getGrade());
                    }
                } else {
                    UserDomainOptionDto newSkill = UserDomainOptionDto.builder()
                            .domainId(cvSkill.getDomainId())
                            .domainOptionId(cvSkill.getDomainOptionId())
                            .grade(cvSkill.getGrade())
                            .build();
                    userDomainOptions.add(newSkill);
                    existingSkillsMap.put(newSkill.getDomainOptionId(), newSkill);
                }
            }
        }
        userDto.setUserDomainOptions(userDomainOptions);
    }
}
