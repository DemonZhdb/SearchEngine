package app.DTO;

import app.model.StatusIndexing;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
public class Detailed {

    private String url;

    private String name;

    private StatusIndexing status;

    private Timestamp statusTime;

    private String lastError;

    private long pages;

    private long lemmas;


}
