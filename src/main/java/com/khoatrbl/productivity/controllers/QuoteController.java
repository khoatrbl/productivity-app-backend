package com.khoatrbl.productivity.controllers;

import com.khoatrbl.productivity.domains.dtos.CreateQuoteRequest;
import com.khoatrbl.productivity.domains.dtos.QuoteDto;
import com.khoatrbl.productivity.domains.entities.Quotes;
import com.khoatrbl.productivity.services.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        List<QuoteDto> res = quoteList.stream().map(quote ->
                QuoteDto.builder()
                        .quoteId(quote.getId())
                        .text(quote.getText())
                        .author(quote.getAuthor())
                        .category(quote.getCategory())
                        .build()
        ).toList();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping(path = "/random")
    public ResponseEntity<QuoteDto> getRandomQuote() {
        Quotes quote = quoteService.getRandomQuote();

        QuoteDto dto = QuoteDto.builder()
                .quoteId(quote.getId())
                .text(quote.getText())
                .author(quote.getAuthor())
                .category(quote.getCategory())
                .build();

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<QuoteDto> createQuote(@Valid @RequestBody CreateQuoteRequest createQuoteRequest) {
        Quotes quote = quoteService.createQuote(createQuoteRequest);

        QuoteDto dto = QuoteDto.builder()
                .quoteId(quote.getId())
                .text(quote.getText())
                .author(quote.getAuthor())
                .category(quote.getCategory())
                .build();

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @DeleteMapping(path ="/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable("id") UUID quoteId) {
        quoteService.deleteQuote(quoteId);

        return ResponseEntity.noContent().build();
    }
}
