package com.example.DIVE2025.domain.transporterRequest.service;

import com.example.DIVE2025.domain.shelter.dto.GetUsernameRequestDto;
import com.example.DIVE2025.domain.shelter.mapper.ShelterMapper;
import com.example.DIVE2025.domain.transferRequest.Mapper.TransferMapper;
import com.example.DIVE2025.domain.transferRequest.dto.UpdateTfrStatusRequestByTprDto;
import com.example.DIVE2025.domain.transferRequest.service.TransferService;
import com.example.DIVE2025.domain.transporterRequest.dto.*;
import com.example.DIVE2025.domain.transporterRequest.entity.TransportRequest;
import com.example.DIVE2025.domain.transporterRequest.mapper.TransportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TransportService {

    private final TransportMapper transportMapper;
    private final TransferMapper transferMapper;
    private final ShelterMapper shelterMapper;
    private final TransferService transferService;

    @Autowired
    public TransportService(TransportMapper transportMapper, TransferMapper transferMapper, ShelterMapper shelterMapper, TransferService transferService) {
        this.transportMapper = transportMapper;
        this.transferMapper = transferMapper;
        this.shelterMapper = shelterMapper;
        this.transferService = transferService;
    }

    /**
     * fromShelter 위치로 가까운 운송업체 리스트 반환
     */
    public List<RecommendTransporterResponseDto> findTransporterByFromShelter(RecommendTransporterRequestDto recommendTransporterRequestDto) {
        return transportMapper.recommendTransportId(recommendTransporterRequestDto);
    }

    public int saveTransportRequest(TransportRequestSaveDto dto) {

        FindResultDto isExist = transportMapper.findTransportRequestIdByTransferReqeustId(dto.getTransferRequestId());
        if(isExist != null){
            throw new IllegalStateException("transport request already exist");
        }

        TransportRequest entity = TransportRequest.builder()
                .transferRequestId(dto.getTransferRequestId())
                .transporterId(dto.getTransporterId())
                .fromShelterName(shelterMapper.getUsernameById(new GetUsernameRequestDto(dto.getFromShelterId())).getUsername())
                .toShelterName(shelterMapper.getUsernameById(new GetUsernameRequestDto(dto.getToShelterId())).getUsername())
                .message(dto.getMessage())
                .build();

        log.info("save transport-request: {}", entity.toString());
        return transportMapper.saveTransportRequest(entity);
    }

    @Transactional
    public int updateTransportRequest(UpdateTprRequestDto dto) {
        long curVersionForLock = transportMapper.getCurVersionForLock(dto.getId());
        dto.setVersion(curVersionForLock);

        int i = transportMapper.updateTransportRequestStatus(dto);

        if(i != 1){
            throw new OptimisticLockingFailureException("update transport request status failed");
        }

        Long transporterId = transportMapper.getTransporterIdById(dto.getId()).getTransporterId();

        // TransporterRequest -> TransferRequest 업데이트
        long curVersionForTrLock = transferMapper.getCurVersionForLock(dto.getTransferRequestId());
        UpdateTfrStatusRequestByTprDto updateDto = UpdateTfrStatusRequestByTprDto.builder()
                .id(dto.getTransferRequestId())
                .transporterId(transporterId)
                .message(dto.getMessage())
                .tprDecisionStatus(dto.getDecisionStatus())
                .version(curVersionForTrLock)
                .build();

        int result = transferService.updateTfrStatusByTpr(updateDto);
        if(result != 1){
            throw new OptimisticLockingFailureException("update transfer request status (by Transport Decision status)failed");
        }

        return result;
    }

    public int deleteTransportRequest(Long transferRequestId) {
        long curVersionForLock = transportMapper.getCurVersionForLock(transferRequestId);
        TprDeleteRequestDto tprDeleteRequestDto = TprDeleteRequestDto.builder()
                .id(transferRequestId)
                .version(curVersionForLock)
                .build();

        return transportMapper.deleteTransportRequest(tprDeleteRequestDto);
    }

    public List<TprListResponseDto> getAllRequestsByTransporterId(Long transporterId) {
        return transportMapper.getAllRequestByTransporterId(transporterId);
    }


}
