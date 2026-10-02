package com.khoatrbl.productivity.domains.dtos;

import com.khoatrbl.productivity.domains.TreatTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetTreatRequest {
    private UUID id;
    private String treatTier;
}
