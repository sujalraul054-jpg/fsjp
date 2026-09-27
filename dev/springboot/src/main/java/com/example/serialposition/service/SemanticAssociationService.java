package com.example.serialposition.service;

import com.example.serialposition.dto.PositionResultDto;
import com.example.serialposition.dto.SemanticAssociationDto;
import com.example.serialposition.dto.WordAssociationReferenceDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * EXPERIMENTAL / RESEARCH FEATURE — not a validated measure of Spreading
 * Activation Theory. This checks a tiny, hardcoded word-association
 * dataset (no external AI, per the spec) to flag cases where the user
 * missed a word outright but produced something loosely associated with
 * it, either in their recalled-words list (as an intrusion) or in the
 * separate optional "related words" field.
 *
 * Each original word maps to a short LIST of plausible associations
 * rather than just one — different people free-associate differently
 * (e.g. "star" might bring "sky" or "space" to mind), so a single fixed
 * answer per word missed too many reasonable matches.
 *
 * This dataset's keys must cover every word in WordPoolService's pool —
 * the two are kept as separate static lists rather than one shared
 * source, so if you add a new word to the pool, add its associations
 * here too.
 */
@Service
public class SemanticAssociationService {

    private static final Map<String, List<String>> WORD_ASSOCIATIONS = Map.ofEntries(
            Map.entry("apple", List.of("fruit")),
            Map.entry("table", List.of("chair", "furniture")),
            Map.entry("river", List.of("water", "stream")),
            Map.entry("phone", List.of("call", "mobile")),
            Map.entry("cloud", List.of("rain", "sky")),
            Map.entry("house", List.of("home")),
            Map.entry("green", List.of("grass", "color")),
            Map.entry("tiger", List.of("stripes", "wild", "animal")),
            Map.entry("book", List.of("reading", "page")),
            Map.entry("star", List.of("sky", "space")),
            Map.entry("chair", List.of("table", "sit")),
            Map.entry("dog", List.of("bark", "pet", "animal")),
            Map.entry("mountain", List.of("climb", "peak", "snow")),
            Map.entry("ocean", List.of("wave", "water", "blue")),
            Map.entry("bread", List.of("bakery", "food", "toast")),
            Map.entry("clock", List.of("time", "watch")),
            Map.entry("flower", List.of("garden", "petal", "bloom")),
            Map.entry("train", List.of("track", "station", "travel")),
            Map.entry("moon", List.of("night", "space", "star")),
            Map.entry("fire", List.of("heat", "flame", "burn")),
            Map.entry("snow", List.of("cold", "winter", "white")),
            Map.entry("guitar", List.of("music", "string", "play")),
            Map.entry("bridge", List.of("river", "cross")),
            Map.entry("candle", List.of("flame", "wax", "light")),
            Map.entry("shoe", List.of("foot", "wear"))
    );

    public List<SemanticAssociationDto> findAssociations(List<PositionResultDto> positionResults,
                                                           List<String> recalledWords,
                                                           List<String> relatedWords) {
        Set<String> producedWords = new HashSet<>();
        if (recalledWords != null) {
            recalledWords.forEach(w -> producedWords.add(normalize(w)));
        }
        if (relatedWords != null) {
            relatedWords.forEach(w -> producedWords.add(normalize(w)));
        }

        List<SemanticAssociationDto> found = new ArrayList<>();
        for (PositionResultDto result : positionResults) {
            if (result.isRecalled()) {
                continue; // only interested in words the user missed outright
            }
            List<String> candidates = WORD_ASSOCIATIONS.get(normalize(result.getWord()));
            if (candidates == null) {
                continue;
            }
            for (String candidate : candidates) {
                if (producedWords.contains(normalize(candidate))) {
                    found.add(new SemanticAssociationDto(result.getWord(), candidate));
                }
            }
        }
        return found;
    }

    // Reference info for the results page's "Semantic Relations" section —
    // shows the known association candidates for every word in this
    // attempt's list, regardless of whether it was recalled or matched.
    // Purely informational, separate from findAssociations()'s actual hits.
    public List<WordAssociationReferenceDto> getReferenceForWords(List<String> words) {
        List<WordAssociationReferenceDto> references = new ArrayList<>();
        for (String word : words) {
            List<String> candidates = WORD_ASSOCIATIONS.get(normalize(word));
            if (candidates != null && !candidates.isEmpty()) {
                references.add(new WordAssociationReferenceDto(word, candidates));
            }
        }
        return references;
    }

    private String normalize(String word) {
        return word == null ? "" : word.trim().toLowerCase();
    }
}
