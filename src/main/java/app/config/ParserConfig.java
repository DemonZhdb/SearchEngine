package app.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "configparse")
public class ParserConfig {
    private String useragent;
    private String referrer;
    private int timeout;
}
