package app.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Total {

    private long sites;

    private long pages;

    private long lemmas;

    private boolean isIndexing;

}
