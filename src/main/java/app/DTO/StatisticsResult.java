package app.DTO;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class StatisticsResult {

    private String result;

    private StatisticsOut statistics;

    public StatisticsResult(String result, StatisticsOut statistics) {
        this.result = result;
        this.statistics = statistics;
    }
}


