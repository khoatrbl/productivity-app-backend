package com.khoatrbl.productivity.domains.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePasswordRequest {
    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "Password must contain at least one uppercase, one number and one special character."
    )
    private String oldPassword;

    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 100, message = "Password must be between {min} and {max} characters.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).+$",
            message = "Password must contain at least one uppercase, one number and one special character."
    )
    private String newPassword;

    @NotBlank(message = "Password confirmation is required.")
    private String confirmNewPassword;
}
