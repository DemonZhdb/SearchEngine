package app.controller;

import app.DTO.response.ResponseError;
import app.config.SiteConfig;
import app.model.Site;
import app.service.EntityService;
import app.service.IndexService;
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
public class IndexController {
    @Autowired
    EntityService entityService;
    @Autowired
    IndexService indexService;
    @Autowired
    SiteConfig siteConfig;


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
            ParserConfigurationException {
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
            return ResponseEntity.badRequest().body(new ResponseError("Данная страница находится " +
                    "за пределами сайтов,указаных в конфигурационном файле."));
        }
        return ResponseEntity.badRequest().body(new ResponseError("Индексация уже запущена. " +
                "Остановите индексацию, или дождитесь ее окончания"));
    }
}
