package app.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StatisticsOut {

    private Total total;

    private List<Detailed> detailed;
}
