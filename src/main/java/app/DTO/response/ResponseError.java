package app.DTO.response;

import lombok.Data;

@Data

public class ResponseError {
    private final String result = "false";
    private String error;

    public ResponseError(String error) {
        this.error = error;
    }
}
