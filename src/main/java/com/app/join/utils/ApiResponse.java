package com.app.join.utils;

// Beispiel: ApiResponse.java
public class ApiResponse<T> {
    private String status;
    private T data;
    private String message;

    // Konstruktor für Erfolg
    public ApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }

    // Konstruktor für Fehler/Nachricht
    public ApiResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", message);
    }

    public String getStatus() { return status; }
    public T getData() { return data; }
    public String getMessage() { return message; }
}