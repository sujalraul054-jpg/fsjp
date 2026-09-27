package com.example.serialposition.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * What the frontend sends to POST /api/recall.
 */
public class RecallRequestDto {

    @NotEmpty(message = "originalWords must not be empty")
    private List<String> originalWords;

    @NotEmpty(message = "recalledWords must not be empty")
    private List<String> recalledWords;

    // Optional (Phase 7): words the user says came to mind that weren't
    // in the original list. No @NotEmpty here — this field is allowed to
    // be missing or empty, unlike the two above.
    private List<String> relatedWords;

    public RecallRequestDto() {
    }

    public RecallRequestDto(List<String> originalWords, List<String> recalledWords) {
        this.originalWords = originalWords;
        this.recalledWords = recalledWords;
    }

    public List<String> getRelatedWords() {
        return relatedWords;
    }

    public void setRelatedWords(List<String> relatedWords) {
        this.relatedWords = relatedWords;
    }

    public List<String> getOriginalWords() {
        return originalWords;
    }

    public void setOriginalWords(List<String> originalWords) {
        this.originalWords = originalWords;
    }

    public List<String> getRecalledWords() {
        return recalledWords;
    }

    public void setRecalledWords(List<String> recalledWords) {
        this.recalledWords = recalledWords;
    }
}
