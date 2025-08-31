package com.example.DIVE2025.domain.transferRequest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrDeleteRequestDto {
    private Long id;
    private Long version;
}
