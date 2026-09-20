package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.dtos.CreateQuoteRequest;
import com.khoatrbl.productivity.domains.entities.Quotes;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.repositories.QuoteRepository;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.services.QuoteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuoteServiceImpl implements QuoteService {
    private final QuoteRepository quoteRepository;
    private final UserRepository userRepository;

    @Override
    public List<Quotes> getAllQuotes() {
        return quoteRepository.findAll();
    }

    @Override
    public Quotes getDailyQuote(UUID currentUserId) {
        long totalQuotes = quoteRepository.count();

        if (totalQuotes == 0) {
            throw new IllegalStateException("No quote available.");
        }

        ZoneId userZone = resolveUserZone(currentUserId);
        LocalDate todayForUser = LocalDate.now(userZone);
        long daySeed = todayForUser.toEpochDay();

        long offset = Math.floorMod(daySeed, totalQuotes);

        return quoteRepository.findByOffset(offset)
                .orElseThrow(() -> new IllegalStateException("Quote not found for offset " + offset));
    }

    @Override
    public Quotes createQuote(CreateQuoteRequest createQuoteRequest) {
        Quotes quote = Quotes.builder()
                .label(createQuoteRequest.getLabel())
                .title(createQuoteRequest.getTitle())
                .quote(createQuoteRequest.getQuote())
                .author(createQuoteRequest.getAuthor())
                .calmExp(createQuoteRequest.getCalmExp())
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

    private ZoneId resolveUserZone(UUID currentUserId) {
        String storedTimezone = userRepository.findById(currentUserId)
                .map(Users::getTimezone) // adjust getter to your actual User entity
                .orElse(null);

        if (storedTimezone == null) {
            return ZoneOffset.UTC;
        }

        try {
            return ZoneId.of(storedTimezone);
        } catch (DateTimeException e) {
            // Stored value is somehow invalid (corrupted data, a since-renamed
            // IANA zone, etc.) — fall back rather than 500 the whole request.
            return ZoneOffset.UTC;
        }
    }
}
