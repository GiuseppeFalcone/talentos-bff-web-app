package com.certimetergroup.talentos.bffwebapp.controller;

import com.certimetergroup.talentos.bffwebapp.dto.PagedResponseDto;
import com.certimetergroup.talentos.bffwebapp.service.AuthorizationService;
import com.certimetergroup.talentos.bffwebapp.service.rest.DomainApiService;
import com.certimetergroup.talentos.commons.enumeration.ResponseEnum;
import com.certimetergroup.talentos.commons.response.Response;
import com.certimetergroup.talentos.commons.response.dto.domain.CreateDomainDto;
import com.certimetergroup.talentos.commons.response.dto.domain.DomainDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/bff-web-app/domains")
@Tag(name = "BFF Domains", description = "BFF operations for domain data")
@Validated
@RequiredArgsConstructor
public class DomainController {

    private final DomainApiService domainService;
    private final AuthorizationService authorizationService;

    @GetMapping
    public ResponseEntity<Response<PagedResponseDto<DomainDto>>> getDomains(
            @RequestParam(defaultValue = "1") @Positive(message = "Page must be > 0") Integer page,
            @RequestParam(defaultValue = "5") @Positive(message = "Page size must be > 0") Integer pageSize,
            @RequestParam(required = false) String domainName,
            @RequestParam(required = false) String domainOptionValue) {

        PagedResponseDto<DomainDto> pagedResponseDto = domainService.getDomains(page, pageSize, domainName, domainOptionValue);
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, pagedResponseDto));
    }

    @GetMapping("/{domainId}")
    public ResponseEntity<Response<DomainDto>> getDomain(
            @PathVariable @NotNull(message = "Domain Id required") @Positive(message = "Domain Id must be > 0") Long domainId,
            @RequestParam(required = false) Set<Long> domainOptionIds) {

        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, domainService.getDomain(domainId, domainOptionIds)));
    }

    @PostMapping
    public ResponseEntity<Response<DomainDto>> addNewDomain(
            @RequestBody @Valid @NotNull(message = "CreateDomainDto required") CreateDomainDto createDomainDto) {

        authorizationService.checkWriteDomain();
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, domainService.addNewDomain(createDomainDto)));
    }

    @PutMapping("/{domainId}")
    public ResponseEntity<Response<DomainDto>> replaceDomainData(
            @PathVariable @NotNull(message = "Domain Id required") @Positive(message = "Wrong domain id provided") Long domainId,
            @RequestBody @Valid @NotNull(message = "DomainDto required") DomainDto domainDto) {

        authorizationService.checkWriteDomain();
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS, domainService.replaceDomainData(domainId, domainDto)));
    }

    @DeleteMapping("/{domainId}")
    public ResponseEntity<Response<Void>> deleteDomain(
            @PathVariable @NotNull(message = "Domain Id required") @Positive(message = "Wrong domain id provided") Long domainId) {

        authorizationService.checkWriteDomain();
        domainService.deleteDomain(domainId);
        return ResponseEntity.ok().body(new Response<>(ResponseEnum.SUCCESS));
    }
}