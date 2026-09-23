package com.khoatrbl.productivity.services;

import com.khoatrbl.productivity.domains.dtos.CreateQuoteRequest;
import com.khoatrbl.productivity.domains.dtos.QuoteClaimResponse;
import com.khoatrbl.productivity.domains.dtos.QuoteDto;
import com.khoatrbl.productivity.domains.entities.Quotes;

import java.util.List;
import java.util.UUID;

public interface QuoteService {
    List<Quotes> getAllQuotes();

    QuoteDto getDailyQuote(UUID currentUserId);

    QuoteClaimResponse claimDailyQuote(UUID currentUserId);

    Quotes createQuote(CreateQuoteRequest createQuoteRequest);

    void deleteQuote(UUID quoteId);
}
