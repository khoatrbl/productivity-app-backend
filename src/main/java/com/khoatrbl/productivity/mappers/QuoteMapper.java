package com.khoatrbl.productivity.mappers;

import com.khoatrbl.productivity.domains.dtos.QuoteDto;
import com.khoatrbl.productivity.domains.entities.Quotes;

public class QuoteMapper {
    public static QuoteDto toDto(Quotes quote) {
        return QuoteDto.builder()
                .quoteId(quote.getId())
                .title(quote.getTitle())
                .label(quote.getLabel())
                .quote(quote.getQuote())
                .author(quote.getAuthor())
                .calmExp(quote.getCalmExp())
                .build();
    }
}
