package app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("SearchEngine-локальный поисковый движок")
                                .version("1.0")
                                .contact(
                                        new Contact()
                                                .email("zhdb@mail.ru")
                                                .url("https://github.com/DemonZhdb/SearchEngine")
                                                .name("Жуганов Дмитрий")
                                )
                );
    }
}