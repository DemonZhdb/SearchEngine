package app.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultSearch {

    private String site;
    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private float relevance;


}
