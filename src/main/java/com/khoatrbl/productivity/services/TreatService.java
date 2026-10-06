package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.dtos.GetTreatRequest;
import com.khoatrbl.productivity.domains.entities.Treat;

import java.util.List;

public interface TreatService {
    Treat getTreatByTreatId(GetTreatRequest getTreatRequest);

    List<Treat> getAllTreats();
}
