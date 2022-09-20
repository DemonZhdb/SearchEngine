package app.controller;

import app.service.StatisticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StatisticsController {
    @Autowired
    StatisticsService statisticsService;

    @GetMapping("/statistics")
    @Tag(name = "Статистика поисковой системы ", description = "Запрос статистики проиндексированных сайтов/страниц ")
    public ResponseEntity<Object> getStatistics() {

        return ResponseEntity.ok(statisticsService.getStatistics());
    }

}
