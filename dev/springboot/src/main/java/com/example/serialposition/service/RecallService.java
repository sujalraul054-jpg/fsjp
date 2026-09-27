package com.example.serialposition.service;

import com.example.serialposition.dto.PositionResultDto;
import com.example.serialposition.dto.RecallRequestDto;
import com.example.serialposition.dto.RecallResultDto;
import com.example.serialposition.dto.SemanticAssociationDto;
import com.example.serialposition.dto.WordAssociationReferenceDto;
import com.example.serialposition.model.PositionResult;
import com.example.serialposition.model.TestAttempt;
import com.example.serialposition.repository.TestAttemptRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Core scoring logic: matches recalled words against the original list by
 * position, then aggregates primacy / middle / recency percentages. Also
 * persists every attempt so Phase 5 can aggregate across many of them, and
 * checks missed words against the experimental semantic-association
 * dataset (Phase 7).
 *
 * Region boundaries follow the spec for a 10-word list (positions 1-3,
 * 4-7, 8-10), but are written generically so a shorter or longer list
 * degrades sensibly instead of throwing.
 */
@Service
public class RecallService {

    private static final int PRIMACY_END = 3;  // positions 1-3
    private static final int MIDDLE_END = 7;   // positions 4-7
    // recency = everything from MIDDLE_END to the end of the list

    private final TestAttemptRepository testAttemptRepository;
    private final SemanticAssociationService semanticAssociationService;

    public RecallService(TestAttemptRepository testAttemptRepository,
                          SemanticAssociationService semanticAssociationService) {
        this.testAttemptRepository = testAttemptRepository;
        this.semanticAssociationService = semanticAssociationService;
    }

    public RecallResultDto scoreAttempt(RecallRequestDto request) {
        List<String> originalWords = request.getOriginalWords();
        List<String> recalledWords = request.getRecalledWords();
        List<String> relatedWords = request.getRelatedWords();

        Set<String> normalizedRecalled = recalledWords.stream()
                .map(this::normalize)
                .collect(Collectors.toSet());

        List<PositionResultDto> positionResults = new ArrayList<>();
        for (int i = 0; i < originalWords.size(); i++) {
            String word = originalWords.get(i);
            boolean recalled = normalizedRecalled.contains(normalize(word));
            positionResults.add(new PositionResultDto(i + 1, word, recalled));
        }

        int totalWords = originalWords.size();
        long totalRecalled = positionResults.stream().filter(PositionResultDto::isRecalled).count();

        int primacyEnd = Math.min(PRIMACY_END, totalWords);
        int middleEnd = Math.min(MIDDLE_END, totalWords);

        double recallPercentage = percentage((int) totalRecalled, totalWords);
        double primacyScore = regionPercentage(positionResults, 0, primacyEnd);
        double middleScore = regionPercentage(positionResults, primacyEnd, middleEnd);
        double recencyScore = regionPercentage(positionResults, middleEnd, totalWords);

        List<SemanticAssociationDto> semanticAssociations =
                semanticAssociationService.findAssociations(positionResults, recalledWords, relatedWords);

        Set<String> normalizedOriginal = originalWords.stream().map(this::normalize).collect(Collectors.toSet());
        List<String> duplicateWords = findDuplicates(recalledWords);
        List<String> unknownWords = recalledWords.stream()
                .filter(w -> !normalizedOriginal.contains(normalize(w)))
                .map(this::normalize)
                .distinct()
                .collect(Collectors.toList());

        List<WordAssociationReferenceDto> associationReference =
                semanticAssociationService.getReferenceForWords(originalWords);

        persistAttempt(originalWords, recalledWords, totalWords, (int) totalRecalled, recallPercentage, positionResults);

        return new RecallResultDto(
                totalWords,
                (int) totalRecalled,
                recallPercentage,
                primacyScore,
                middleScore,
                recencyScore,
                positionResults,
                semanticAssociations,
                duplicateWords,
                unknownWords,
                associationReference
        );
    }

    // Words that appear more than once in the user's recall (after
    // normalizing case/whitespace) — flagged for the results screen,
    // not treated as an error. Each duplicate is reported once.
    private List<String> findDuplicates(List<String> words) {
        Map<String, Long> counts = words.stream()
                .collect(Collectors.groupingBy(this::normalize, Collectors.counting()));
        Set<String> seen = new HashSet<>();
        List<String> duplicates = new ArrayList<>();
        for (String word : words) {
            String norm = normalize(word);
            if (counts.get(norm) > 1 && seen.add(norm)) {
                duplicates.add(norm);
            }
        }
        return duplicates;
    }

    private void persistAttempt(List<String> originalWords, List<String> recalledWords, int totalWords,
                                 int totalRecalled, double recallPercentage, List<PositionResultDto> positionResults) {
        TestAttempt attempt = new TestAttempt();
        attempt.setTimestamp(LocalDateTime.now());
        attempt.setOriginalWords(String.join(",", originalWords));
        attempt.setRecalledWords(String.join(",", recalledWords));
        attempt.setTotalWords(totalWords);
        attempt.setTotalRecalled(totalRecalled);
        attempt.setRecallPercentage(recallPercentage);

        for (PositionResultDto dto : positionResults) {
            attempt.addPositionResult(new PositionResult(dto.getPosition(), dto.getWord(), dto.isRecalled()));
        }

        testAttemptRepository.save(attempt);
    }

    private double regionPercentage(List<PositionResultDto> results, int startInclusive, int endExclusive) {
        if (endExclusive <= startInclusive) {
            return 0.0;
        }
        List<PositionResultDto> slice = results.subList(startInclusive, endExclusive);
        long hits = slice.stream().filter(PositionResultDto::isRecalled).count();
        return percentage((int) hits, slice.size());
    }

    private double percentage(int part, int total) {
        if (total == 0) {
            return 0.0;
        }
        // Round to 2 decimal places.
        return Math.round((part * 10000.0) / total) / 100.0;
    }

    private String normalize(String word) {
        return word == null ? "" : word.trim().toLowerCase();
    }
}
