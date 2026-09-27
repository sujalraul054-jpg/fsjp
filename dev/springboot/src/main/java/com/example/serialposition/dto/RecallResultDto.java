package com.example.serialposition.dto;

import java.util.List;

/**
 * What POST /api/recall returns for a single attempt.
 */
public class RecallResultDto {

    private int totalWords;
    private int totalRecalled;
    private double recallPercentage;
    private double primacyScore;
    private double middleScore;
    private double recencyScore;
    private List<PositionResultDto> positionResults;
    private List<SemanticAssociationDto> semanticAssociations;
    private List<String> duplicateWords;
    private List<String> unknownWords;
    private List<WordAssociationReferenceDto> associationReference;

    public RecallResultDto() {
    }

    public RecallResultDto(int totalWords, int totalRecalled, double recallPercentage,
                            double primacyScore, double middleScore, double recencyScore,
                            List<PositionResultDto> positionResults,
                            List<SemanticAssociationDto> semanticAssociations,
                            List<String> duplicateWords,
                            List<String> unknownWords,
                            List<WordAssociationReferenceDto> associationReference) {
        this.totalWords = totalWords;
        this.totalRecalled = totalRecalled;
        this.recallPercentage = recallPercentage;
        this.primacyScore = primacyScore;
        this.middleScore = middleScore;
        this.recencyScore = recencyScore;
        this.positionResults = positionResults;
        this.semanticAssociations = semanticAssociations;
        this.duplicateWords = duplicateWords;
        this.unknownWords = unknownWords;
        this.associationReference = associationReference;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    public int getTotalRecalled() {
        return totalRecalled;
    }

    public void setTotalRecalled(int totalRecalled) {
        this.totalRecalled = totalRecalled;
    }

    public double getRecallPercentage() {
        return recallPercentage;
    }

    public void setRecallPercentage(double recallPercentage) {
        this.recallPercentage = recallPercentage;
    }

    public double getPrimacyScore() {
        return primacyScore;
    }

    public void setPrimacyScore(double primacyScore) {
        this.primacyScore = primacyScore;
    }

    public double getMiddleScore() {
        return middleScore;
    }

    public void setMiddleScore(double middleScore) {
        this.middleScore = middleScore;
    }

    public double getRecencyScore() {
        return recencyScore;
    }

    public void setRecencyScore(double recencyScore) {
        this.recencyScore = recencyScore;
    }

    public List<PositionResultDto> getPositionResults() {
        return positionResults;
    }

    public void setPositionResults(List<PositionResultDto> positionResults) {
        this.positionResults = positionResults;
    }

    public List<SemanticAssociationDto> getSemanticAssociations() {
        return semanticAssociations;
    }

    public void setSemanticAssociations(List<SemanticAssociationDto> semanticAssociations) {
        this.semanticAssociations = semanticAssociations;
    }

    public List<String> getDuplicateWords() {
        return duplicateWords;
    }

    public void setDuplicateWords(List<String> duplicateWords) {
        this.duplicateWords = duplicateWords;
    }

    public List<String> getUnknownWords() {
        return unknownWords;
    }

    public void setUnknownWords(List<String> unknownWords) {
        this.unknownWords = unknownWords;
    }

    public List<WordAssociationReferenceDto> getAssociationReference() {
        return associationReference;
    }

    public void setAssociationReference(List<WordAssociationReferenceDto> associationReference) {
        this.associationReference = associationReference;
    }
}
