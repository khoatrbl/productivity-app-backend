package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.QuoteDto;
import com.khoatrbl.productivity.domains.entities.Quotes;

public class QuoteMapper {
    public static QuoteDto toDto(Quotes quote, boolean alreadyClaimed) {
        return QuoteDto.builder()
                .label(quote.getLabel())
                .title(quote.getTitle())
                .quote(quote.getQuote())
                .author(quote.getAuthor())
                .calmExp(quote.getCalmExp())
                .alreadyClaimed(alreadyClaimed)
                .build();
    }
}
