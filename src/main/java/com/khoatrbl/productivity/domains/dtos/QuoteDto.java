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
    private String label;
    private String title;
    private String quote;
    private String author;
    private int calmExp;
    private boolean alreadyClaimed;
}
