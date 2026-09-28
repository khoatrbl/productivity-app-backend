package com.khoatrbl.productivity.domains.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdatePetNameRequest {
    @NotBlank(message = "Pet name is required.")
    @Size(min = 3, max = 15, message = "Pet name must be between {min} and {max} characters.")
    @Pattern(
            regexp = "^[^\\r\\n]*$",
            message = "Pet name cannot contain line breaks."
    )
    private String name;
}
