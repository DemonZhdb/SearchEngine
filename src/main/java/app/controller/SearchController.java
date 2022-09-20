package app.controller;

import app.DTO.response.ResponseError;
import app.service.SearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Поиск текста", description = "Поиск строки текста на сайтах из списка в конфигурации")
public class SearchController {

    @Autowired
    SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "site", required = false) String site,
            @RequestParam(value = "offset", defaultValue = "0", required = false) int offset,
            @RequestParam(value = "limit", defaultValue = "20", required = false) int limit)
            throws SQLException, IOException {
        if (query.isEmpty()) {
            return ResponseEntity.badRequest().body(new ResponseError("Задан пустой поисковый запрос"));
        }
        return ResponseEntity.ok(searchService.search(query, site, offset, limit));
    }



}
