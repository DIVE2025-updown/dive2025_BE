package com.example.DIVE2025.domain.shelter.service;

import com.example.DIVE2025.domain.rescued.enums.AnimalCondition;
import com.example.DIVE2025.domain.shelter.dto.RecommendRequestDto;
import com.example.DIVE2025.domain.shelter.dto.RecommendResponseDto;
import com.example.DIVE2025.domain.shelter.dto.UpdateCapacityRequestDto;
import com.example.DIVE2025.domain.shelter.mapper.ShelterMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
@Transactional
class ShelterServiceTest {

    @Autowired
    private ShelterService shelterService;
    @Autowired
    private ShelterMapper shelterMapper;

    @Test
    void recommendShelter(){
        RecommendRequestDto rescued = RecommendRequestDto.builder()
                .animalCondition(AnimalCondition.SEVERE)
                .latitude(35.151443)
                .longitude(128.93962)
                .build();

        List<RecommendResponseDto> shelterPriority = shelterService.findShelterPriority(rescued);
        Assertions.assertThat(shelterPriority.size()).isEqualTo(3);

        System.out.println(shelterPriority);
    }

    @Test
    void saveCapacity(){
        UpdateCapacityRequestDto normalTest = UpdateCapacityRequestDto.builder()
                .shelterId(1L)
                .curCapacity(10L)
                .totalCapacity(100L)
                .build();

        int i = shelterService.updateShelterCapacity(normalTest);
        Assertions.assertThat(i).isEqualTo(1);

        UpdateCapacityRequestDto failTest = UpdateCapacityRequestDto.builder()
                .shelterId(1L)
                .curCapacity(1000L)
                .totalCapacity(100L)
                .build();

        Assertions.assertThatThrownBy(() -> shelterService.updateShelterCapacity(failTest)).isInstanceOf(IllegalArgumentException.class);
    }
}