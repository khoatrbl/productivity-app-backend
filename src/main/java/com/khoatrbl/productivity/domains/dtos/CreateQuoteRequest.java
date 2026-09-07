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
    @NotBlank(message = "Quote content is required.")
    @Size(max = 2000, message = "Quote text cannot exceed {max} characters.")
    @Pattern(
            regexp = "^[^\\r\\n]*$",
            message = "Quote text cannot contain line breaks."
    )
    private String text;

    @NotBlank(message = "Quote author is required.")
    @Size(max = 255, message = "Author name cannot exceed {max} characters.")
    private String author;

    @NotNull(message = "Quote category is required.")
    private QuoteCategory quoteCategory;
}
