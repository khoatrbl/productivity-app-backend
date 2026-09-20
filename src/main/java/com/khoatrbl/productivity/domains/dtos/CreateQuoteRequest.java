package com.khoatrbl.productivity.domains.dtos;

import com.khoatrbl.productivity.domains.QuoteCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateQuoteRequest {

    @NotBlank(message = "Quote label is required.")
    @Size(max = 20, message = "Quote label must not exceed {max} characters.")
    @Pattern(
            regexp = "^[^\\r\\n]*$",
            message = "Quote label must not contain line breaks."
    )
    private String label;

    @NotBlank(message = "Quote title is required.")
    @Size(max = 25, message = "Quote title cannot exceed {max} characters.")
    @Pattern(
            regexp = "^[^\\r\\n]*$",
            message = "Quote title cannot contain line breaks."
    )
    private String title;

    @NotBlank(message = "Quote is required.")
    @Size(max = 120, message = "Quote must not exceed {max} characters.")
    private String quote;

    @NotBlank(message = "Quote author is required.")
    @Size(max = 30, message = "Author name cannot exceed {max} characters.")
    private String author;

    @NotNull(message = "Quote's EXP is required.")
    private int calmExp;
}
