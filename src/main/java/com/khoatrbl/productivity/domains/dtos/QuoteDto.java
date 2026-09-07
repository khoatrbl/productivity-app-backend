package com.khoatrbl.productivity.domains.dtos;

import com.khoatrbl.productivity.domains.QuoteCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuoteDto {
    private UUID quoteId;
    private String text;
    private String author;
    private QuoteCategory category;
}
