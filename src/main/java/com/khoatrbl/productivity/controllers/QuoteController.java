package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.domains.dtos.CreateQuoteRequest;
import com.khoatrbl.productivity.domains.dtos.QuoteClaimResponse;
import com.khoatrbl.productivity.domains.dtos.QuoteDto;
import com.khoatrbl.productivity.domains.entities.Quotes;
import com.khoatrbl.productivity.mappers.QuoteMapper;
import com.khoatrbl.productivity.services.QuoteService;
import com.khoatrbl.productivity.utilities.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/quotes")
@RequiredArgsConstructor
public class QuoteController {
    private final QuoteService quoteService;

    @GetMapping
    public ResponseEntity<List<QuoteDto>> getAllQuotes() {
        List<Quotes> quoteList = quoteService.getAllQuotes();

        List<QuoteDto> res = quoteList.stream().map(quote -> QuoteMapper.toDto(quote, false)).toList();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(path = "/daily")
    public ResponseEntity<QuoteDto> getDailyQuote(Authentication authentication) {
        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);

        QuoteDto dto = quoteService.getDailyQuote(currentUserId);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<QuoteDto> createQuote(@Valid @RequestBody CreateQuoteRequest createQuoteRequest) {
        Quotes quote = quoteService.createQuote(createQuoteRequest);

        QuoteDto dto = QuoteMapper.toDto(quote, false);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PatchMapping(path = "/claims")
    public ResponseEntity<QuoteClaimResponse> claimDailyQuote(Authentication authentication) {
        UUID currentUserId = SecurityUtils.getCurrentUserId(authentication);
        return ResponseEntity.ok(quoteService.claimDailyQuote(currentUserId));
    }

    @DeleteMapping(path ="/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable("id") UUID quoteId) {
        quoteService.deleteQuote(quoteId);

        return ResponseEntity.noContent().build();
    }
}
