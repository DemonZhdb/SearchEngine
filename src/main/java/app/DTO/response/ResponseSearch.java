package app.DTO.response;

import app.DTO.ResultSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class ResponseSearch {
    private String result = "true";
    private int count;
    private List<ResultSearch> data;

    public ResponseSearch(int count, List<ResultSearch> data) {
        this.count = count;
        this.data = data;
    }
}
