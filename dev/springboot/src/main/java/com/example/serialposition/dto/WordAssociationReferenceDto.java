package com.example.serialposition.dto;

import java.util.List;

/**
 * Reference info for the results page's "Semantic Relations" section: a
 * word from this attempt's list, and whatever's in the hardcoded
 * association dataset for it — shown regardless of whether it was
 * recalled or actually matched anything the user wrote.
 */
public class WordAssociationReferenceDto {

    private String word;
    private List<String> knownAssociations;

    public WordAssociationReferenceDto() {
    }

    public WordAssociationReferenceDto(String word, List<String> knownAssociations) {
        this.word = word;
        this.knownAssociations = knownAssociations;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<String> getKnownAssociations() {
        return knownAssociations;
    }

    public void setKnownAssociations(List<String> knownAssociations) {
        this.knownAssociations = knownAssociations;
    }
}
