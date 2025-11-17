package com.certimetergroup.easycv.bffwebapp.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PagedResponseDto<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
}
