package com.khoatrbl.productivity.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StartTaskExpClaimResponse {
    private boolean claimed;
    private int expGranted;
    private ProfileDto profile;
}
