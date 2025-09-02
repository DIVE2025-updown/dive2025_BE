package com.example.DIVE2025.domain.rescued.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RescuedByDateRequestDto {
    private Long shelterId;
    private String date;
}
