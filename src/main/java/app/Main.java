package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "app.repository")
@EnableAsync
public class Main {
    public static void main(String[] args) {
                SpringApplication.run(app.Main .class, args);
    }

}
