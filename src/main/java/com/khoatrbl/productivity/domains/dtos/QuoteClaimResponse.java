package com.khoatrbl.productivity.domains.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuoteClaimResponse {
    private boolean claimed;   // false if today was already claimed — idempotent no-op
    private int expGranted;     // 0 when claimed=false
    private ProfileDto profile; // reuses your existing /me shape, always current regardless of claimed
}
