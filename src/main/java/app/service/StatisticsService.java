package app.service;

import app.DTO.Detailed;
import app.DTO.StatisticsOut;
import app.DTO.StatisticsResult;
import app.DTO.Total;
import app.repository.LemmaRepository;
import app.repository.PageRepository;
import app.repository.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsService {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private LemmaRepository lemmaRepository;

    public StatisticsResult getStatistics() {

        Total total = new Total(siteRepository.count(), pageRepository.count(),
                lemmaRepository.count(), true);

        List<Detailed> detailedList = new ArrayList<>();
        siteRepository.findAll().forEach(site -> {
            Detailed detailed = new Detailed(site.getUrl(), site.getName(), site.getStatus(),
                    site.getStatusTime(), site.getLastError(), pageRepository.countBySiteByPage(site),
                    lemmaRepository.countBySiteByLemma(site));
            detailedList.add(detailed);
        });
        StatisticsOut statisticsOut = new StatisticsOut(total, detailedList) {

        };

        return new StatisticsResult("true", statisticsOut);
    }

    ;
}
