package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.dtos.GetTreatRequest;
import com.khoatrbl.productivity.domains.dtos.PurchaseRequest;
import com.khoatrbl.productivity.domains.entities.Treat;

import java.util.List;
import java.util.UUID;

public interface TreatService {
    Treat getTreatByTreatId(GetTreatRequest getTreatRequest);

    List<Treat> getAllTreats();
}
