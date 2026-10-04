package com.khoatrbl.productivity.domains.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchaseRequest {
    @Min(value = 1, message = "Quantity must be at least 1.")
    @Max(value = 10, message = "Quantity can only be at most 10.")
    private int quantity;
}
