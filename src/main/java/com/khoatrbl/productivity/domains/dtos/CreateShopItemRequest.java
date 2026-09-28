package com.khoatrbl.productivity.domains.dtos;

import com.khoatrbl.productivity.domains.ItemType;
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
public class CreateShopItemRequest {
    @NotBlank(message = "Item name is required.")
    @Size(min = 1, max = 25, message = "Item name must be between {min} and {max} characters.")
    @Pattern(
            regexp = "^[^\\r\\n]*$",
            message = "Item name cannot contain line breaks."
    )
    private String name;

    @NotBlank(message = "Item description is required.")
    @Size(max = 100, message = "Item description must be under {max} characters.")
    private String description;

    @NotNull(message = "Item type is required.")
    private ItemType itemType;

    @NotNull(message = "Item price is required.")
    private int price;

    @NotNull(message = "User level is required.")
    private int requiredUserLevel;
}
