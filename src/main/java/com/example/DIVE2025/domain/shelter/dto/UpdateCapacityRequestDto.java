package com.example.DIVE2025.domain.shelter.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UpdateCapacityRequestDto {
    private Long shelterId;
    private Long totalCapacity;
    private Long curCapacity;
}
