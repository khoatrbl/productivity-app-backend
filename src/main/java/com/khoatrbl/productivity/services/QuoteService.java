package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.dtos.CreateQuoteRequest;
import com.khoatrbl.productivity.domains.entities.Quotes;

import java.util.List;
import java.util.UUID;

public interface QuoteService {
    List<Quotes> getAllQuotes();

    Quotes getDailyQuote(UUID currentUserId);

    Quotes createQuote(CreateQuoteRequest createQuoteRequest);

    void deleteQuote(UUID quoteId);
}
