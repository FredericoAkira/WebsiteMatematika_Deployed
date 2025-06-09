package dev.fredericoAkira.FtoA.Configuration;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String status;
    private String message;
    private T data;

    // Static factory methods for consistency
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "OK", "Successful", data);
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(status.value(), status.getReasonPhrase(), message, null);
    }
}
