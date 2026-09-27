package com.example.serialposition.dto;

/**
 * A single position's recall rate, averaged across every stored attempt.
 * This is what Chart.js plots in Phase 6 to draw the U-shaped curve.
 */
public class PositionAggregateDto {

    private int position;
    private int timesPresented;   // how many attempts included this position
    private int timesRecalled;    // how many of those recalled it
    private double recallPercentage;

    public PositionAggregateDto() {
    }

    public PositionAggregateDto(int position, int timesPresented, int timesRecalled, double recallPercentage) {
        this.position = position;
        this.timesPresented = timesPresented;
        this.timesRecalled = timesRecalled;
        this.recallPercentage = recallPercentage;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTimesPresented() {
        return timesPresented;
    }

    public void setTimesPresented(int timesPresented) {
        this.timesPresented = timesPresented;
    }

    public int getTimesRecalled() {
        return timesRecalled;
    }

    public void setTimesRecalled(int timesRecalled) {
        this.timesRecalled = timesRecalled;
    }

    public double getRecallPercentage() {
        return recallPercentage;
    }

    public void setRecallPercentage(double recallPercentage) {
        this.recallPercentage = recallPercentage;
    }
}
