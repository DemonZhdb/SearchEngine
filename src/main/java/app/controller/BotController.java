package app.controller;

import app.DTO.response.ResponseError;
import app.config.SiteConfig;
import app.model.Site;
import app.service.EntityService;
import app.service.IndexService;
import app.service.SearchService;
import app.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BotController {
    @Autowired
    EntityService entityService;
    @Autowired
    IndexService indexService;
    @Autowired
    SiteConfig siteConfig;
    @Autowired
    SearchService searchService;
    @Autowired
    StatisticsService statisticsService;


    @GetMapping("/startIndexing")
    public ResponseEntity<Object> startIndexing() {

        if (!entityService.isIndexingRun()) {
            return ResponseEntity.ok(indexService.startIndexing());
        }
        return ResponseEntity.badRequest().body(new ResponseError("Индексация уже запущена"));
    }


    @GetMapping("/stopIndexing")
    public ResponseEntity<Object> stopIndexing() {
        if (entityService.isIndexingRun()) {
            return ResponseEntity.ok(indexService.stopIndexing());
        }
        return ResponseEntity.badRequest().body(new ResponseError("Индексация не запущена"));
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Object> getPage(@RequestParam(name = "url") String url) throws SQLException, IOException,
            InterruptedException, ParserConfigurationException {
        if (!entityService.isIndexingRun()) {

            ArrayList<Site> sites = siteConfig.getSites();
            for (Site site : sites) {
                if (url.toLowerCase().contains(site.getUrl())) {
                    if (!(indexService.getDocumentPage(url, site) == null)) {
                        return ResponseEntity.ok(indexService.indexingPage(url, site));
                    } else {
                        return ResponseEntity.badRequest().body(new ResponseError("Указанная страница не найдена"));
                    }
                }
            }
            return ResponseEntity.badRequest().body(new ResponseError("Данная страница находится за пределами сайтов, " +
                    "указаных в конфигурационном файле."));
        }
        return ResponseEntity.badRequest().body(new ResponseError("Индексация уже запущена. Остановите индексацию, " +
                "или дождитесь ее окончания"));
    }


    @GetMapping("/search")
//    ResponseEntity<Object>
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


    @GetMapping("/statistics")
    public ResponseEntity<Object> getStatistics() {

        return ResponseEntity.ok(statisticsService.getStatistics());
    }

}
