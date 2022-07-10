package app.service;

import app.DTO.RelevancePage;
import app.DTO.ResultSearch;
import app.DTO.response.ResponseSearch;
import app.model.Lemma;
import app.util.Lemmatisator;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class SearchService {
    private int resultsNumber;
    Lemmatisator lemmatisator = new Lemmatisator();
    HashMap<String, Integer> searchLemmaMap;
    @Autowired
    EntityService entityService;
    @Autowired
    IndexService indexService;

    public SearchService() throws IOException {
    }

    public Object search(String querySearch, String siteUrl, int offset, int limit)
            throws SQLException, IOException {

        searchLemmaMap = new HashMap<>();
        searchLemmaMap = lemmatisator.textToLemma(querySearch);
        resultsNumber = 0;

        ArrayList<RelevancePage> resultQueries = new ArrayList<>();
        if (siteUrl == null) {

            entityService.findSites().forEach(site -> {
                entityService.deleteAllQueryLemma();
                List<Lemma> lemmasOfSite = entityService.listLemmaOrderByFrequency(searchLemmaMap, site);

                for (int i = 0; i < lemmasOfSite.size(); i++) {
                    entityService.addQueryLemma(lemmasOfSite.get(i).getLemma(),
                            lemmasOfSite.get(i).getId());
                }
                List<RelevancePage> resultQueriesOfSite = indexService.getRelevancePages(offset, limit);

                resultQueries.addAll(resultQueriesOfSite);
                resultsNumber += indexService.getCountPages();

            });
        } else {
            entityService.deleteAllQueryLemma();
            List<Lemma> lemmasOfSite = entityService.listLemmaOrderByFrequency(searchLemmaMap,
                    entityService.findSiteByUrl(siteUrl));
            for (int i = 0; i < lemmasOfSite.size(); i++) {

                entityService.addQueryLemma(lemmasOfSite.get(i).getLemma(), lemmasOfSite.get(i).getId());
            }
            List<RelevancePage> resultQueriesOfSite = indexService.getRelevancePages(offset, limit);
            resultQueries.addAll(resultQueriesOfSite);
            resultsNumber = indexService.getCountPages();
        }
        Object resultSearch = new ResponseSearch(resultsNumber, createSearchResult(resultQueries,
                querySearch));
        System.out.println(resultSearch);
        return resultSearch;
    }

    private List<ResultSearch> createSearchResult(ArrayList<RelevancePage> relevancePages,
                                                  String querySearch) {
        List<ResultSearch> searchResult = new ArrayList<>();

        relevancePages.forEach(relevancePage -> {
            ResultSearch resultSearch = new ResultSearch();
            resultSearch.setSite(relevancePage.getSite());
            resultSearch.setSiteName(relevancePage.getSiteName());
            resultSearch.setUri(relevancePage.getUri());
            resultSearch.setTitle(Jsoup.parse(relevancePage.getContent()).title());
            resultSearch.setSnippet(lemmatisator.getSnippet(relevancePage.getContent(), querySearch));
            resultSearch.setRelevance(relevancePage.getRelevance());

            searchResult.add(resultSearch);
        });

        return searchResult;
    }
}
