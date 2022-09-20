package app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Tag(name = "Авторизация/стартовая", description = "Авторизации пользователей, меню статистики,поиска и индексации сайтов")
public class DefaultController {

    @GetMapping("${url.default}")
    @Operation(summary = "Стартовая / авторизация ")
    public String index() {
        return "index";
    }
}
