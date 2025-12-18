package com.certimetergroup.talentos.bffwebapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    private long totalElements;
    private int totalPages;
    private int size;
    private int number;
}
