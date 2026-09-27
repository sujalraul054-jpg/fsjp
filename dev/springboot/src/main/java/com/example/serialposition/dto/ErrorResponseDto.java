package com.example.serialposition.dto;

/**
 * Uniform error shape returned to the frontend for 4xx/5xx responses.
 */
public class ErrorResponseDto {

    private int status;
    private String message;

    public ErrorResponseDto() {
    }

    public ErrorResponseDto(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
