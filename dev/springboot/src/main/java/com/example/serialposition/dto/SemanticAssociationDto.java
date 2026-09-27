package com.example.serialposition.dto;

/**
 * One flagged pairing: the user didn't recall originalWord, but produced
 * (in recalledWords or relatedWords) a word this app's small dataset
 * treats as semantically linked to it.
 */
public class SemanticAssociationDto {

    private String originalWord;
    private String associatedWord;

    public SemanticAssociationDto() {
    }

    public SemanticAssociationDto(String originalWord, String associatedWord) {
        this.originalWord = originalWord;
        this.associatedWord = associatedWord;
    }

    public String getOriginalWord() {
        return originalWord;
    }

    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
    }

    public String getAssociatedWord() {
        return associatedWord;
    }

    public void setAssociatedWord(String associatedWord) {
        this.associatedWord = associatedWord;
    }
}
