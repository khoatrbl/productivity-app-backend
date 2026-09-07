package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.dtos.CreateQuoteRequest;
import com.khoatrbl.productivity.domains.entities.Quotes;
import com.khoatrbl.productivity.repositories.QuoteRepository;
import com.khoatrbl.productivity.services.QuoteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {
    private final QuoteRepository quoteRepository;

    @Override
    public List<Quotes> getAllQuotes() {
        return quoteRepository.findAll();
    }

    @Override
    public Quotes getRandomQuote() {
        return quoteRepository.findRandomQuote()
                .orElseThrow(
                        () -> new EntityNotFoundException("Quote not found.")
                );
    }

    @Override
    public Quotes createQuote(CreateQuoteRequest createQuoteRequest) {
        Quotes quote = Quotes.builder()
                .text(createQuoteRequest.getText())
                .author(createQuoteRequest.getAuthor())
                .category(createQuoteRequest.getQuoteCategory())
                .build();

        return quoteRepository.save(quote);
    }

    @Override
    public void deleteQuote(UUID quoteId) {
        Quotes quoteToDelete = quoteRepository.findById(quoteId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Quote not found for id: " + quoteId)
                );

        quoteRepository.delete(quoteToDelete);

    }
}
