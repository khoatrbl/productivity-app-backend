package com.khoatrbl.productivity.services.impl;

import com.khoatrbl.productivity.domains.dtos.*;
import com.khoatrbl.productivity.domains.entities.Quotes;
import com.khoatrbl.productivity.domains.entities.Users;
import com.khoatrbl.productivity.mappers.ProfileMapper;
import com.khoatrbl.productivity.mappers.QuoteMapper;
import com.khoatrbl.productivity.repositories.QuoteRepository;
import com.khoatrbl.productivity.repositories.UserRepository;
import com.khoatrbl.productivity.services.QuoteService;
import com.khoatrbl.productivity.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserService userService; // reuse the real leveling logic — never duplicate it here

    @Override
    public List<Quotes> getAllQuotes() {
        return quoteRepository.findAll();
    }

    @Override
    public QuoteDto getDailyQuote(UUID currentUserId) {
        Users user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + currentUserId));

        ZoneId userZone = resolveZone(user.getTimezone());
        LocalDate todayForUser = LocalDate.now(userZone);

        Quotes quote = fetchQuoteForDate(todayForUser);
        boolean alreadyClaimed = todayForUser.equals(user.getLastQuoteClaimedDate());

        return QuoteMapper.toDto(quote, alreadyClaimed);
    }

    @Override
    @Transactional
    public QuoteClaimResponse claimDailyQuote(UUID currentUserId) {
        Users user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + currentUserId));

        ZoneId userZone = resolveZone(user.getTimezone());
        LocalDate todayForUser = LocalDate.now(userZone);

        if (todayForUser.equals(user.getLastQuoteClaimedDate())) {
            // Already claimed today — idempotent no-op, no matter how this got
            // triggered (relogin, retry, stale client state).
            return QuoteClaimResponse.builder()
                    .claimed(false) // nothing is claimed
                    .expGranted(0)
                    .profile(ProfileMapper.toDto(user))
                    .build();
        }

        Quotes quote = fetchQuoteForDate(todayForUser);
        int calmExp = quote.getCalmExp();

        // Delegates to the SAME leveling logic /me/exp already uses.
        UpdateExpRequest expRequest = UpdateExpRequest.builder().expGained(calmExp).build();
        Users updatedUser = userService.updateUserLevel(currentUserId, expRequest);

        updatedUser.setLastQuoteClaimedDate(todayForUser);
        userRepository.save(updatedUser);

        return QuoteClaimResponse.builder()
                .claimed(true)
                .expGranted(calmExp)
                .profile(ProfileMapper.toDto(updatedUser))
                .build();
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
                .orElseThrow(() -> new EntityNotFoundException("Quote not found for id: " + quoteId));

        quoteRepository.delete(quoteToDelete);
    }

    private Quotes fetchQuoteForDate(LocalDate date) {
        long totalQuotes = quoteRepository.count();
        if (totalQuotes == 0) {
            throw new IllegalStateException("No quote available.");
        }
        long offset = Math.floorMod(date.toEpochDay(), totalQuotes);
        return quoteRepository.findByOffset(offset)
                .orElseThrow(() -> new IllegalStateException("Quote not found for offset " + offset));
    }

    private ZoneId resolveZone(String storedTimezone) {
        if (storedTimezone == null) {
            return ZoneOffset.UTC;
        }
        try {
            return ZoneId.of(storedTimezone);
        } catch (DateTimeException e) {
            return ZoneOffset.UTC;
        }
    }
}