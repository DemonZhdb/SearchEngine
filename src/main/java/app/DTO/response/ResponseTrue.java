package app.DTO.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseTrue {
    private String result;


    public ResponseTrue(String result) {
        this.result = result;
    }

}
