package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.dtos.GetTreatRequest;
import com.khoatrbl.productivity.domains.entities.Treat;
import com.khoatrbl.productivity.repositories.TreatRepository;
import com.khoatrbl.productivity.services.TreatService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatServiceImpl implements TreatService {
    private final TreatRepository treatRepository;

    @Override
    public Treat getTreatByTreatId(GetTreatRequest getTreatRequest) {
        return treatRepository.findById(getTreatRequest.getId())
                .orElseThrow(
                        () -> new EntityNotFoundException("Treat not found for id: " + getTreatRequest.getId())
                );
    }

    @Override
    public List<Treat> getAllTreats() {
        return treatRepository.findAll();
    }


}
