package com.example.serialposition.dto;

/**
 * Recall outcome for one position in the original word list.
 */
public class PositionResultDto {

    private int position;
    private String word;
    private boolean recalled;

    public PositionResultDto() {
    }

    public PositionResultDto(int position, String word, boolean recalled) {
        this.position = position;
        this.word = word;
        this.recalled = recalled;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isRecalled() {
        return recalled;
    }

    public void setRecalled(boolean recalled) {
        this.recalled = recalled;
    }
}
