package app.service;

import app.DTO.RelevancePage;
import app.DTO.response.ResponseTrue;
import app.config.ParserConfig;
import app.config.SiteConfig;
import app.model.Page;
import app.model.Site;
import app.model.StatusIndexing;
import app.repository.IndexRepository;
import app.repository.QueryLemmaRepository;
import app.util.ParserLinks;
import lombok.Data;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

@Data
@Service
public class IndexService {

    @Autowired
    private IndexRepository indexRepository;
    @Autowired
    private QueryLemmaRepository queryLemmaRepository;
    @Autowired
    private SiteConfig siteConfig;
    @Autowired
    ParserConfig parserConfig;
    @Autowired
    private EntityService entityService;
    private Pageable pageable;
    private int codeResponse;

    @Transactional
    public void deleteAllIndexes() {
        indexRepository.deleteAll();
    }

    public Object startIndexing() {
        ArrayList<Site> sites = siteConfig.getSites();

        entityService.setIndexingRun(true);
        entityService.setIndexingStop(false);

        deleteAllIndexes();
        entityService.deleteAllPages();
        entityService.deleteAllLemmas();

        for (Site site : sites) {

            CompletableFuture.runAsync(() -> {
                indexingSite(site);

            }, ForkJoinPool.commonPool());

        }
        return new ResponseTrue("true");
    }

    @Async
    public void indexingSite(Site siteForIndex) {
        if (!(entityService.findSiteByName(siteForIndex) == null)) {
            siteForIndex.setId(entityService.findSiteByName(siteForIndex).getId());

        }
        Site siteInDB = entityService.updateSite(siteForIndex, StatusIndexing.INDEXING);

        Set<String> linksSet = Collections.synchronizedSet(new HashSet<>());
        ParserLinks parserLinks = new ParserLinks(siteInDB.getUrl() + "/", siteInDB, linksSet);
        parserLinks.setParserConfig(parserConfig);
        parserLinks.setEntityService(entityService);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(parserLinks);
        if (entityService.isIndexingStop()) {
            siteInDB.setLastError("Indexing Stopped");
            entityService.updateSite(siteInDB, StatusIndexing.FAILED);
        } else {
            entityService.updateSite(siteInDB, StatusIndexing.INDEXED);
        }
        entityService.setIndexingRun(false);

    }

    public Object stopIndexing() {
        entityService.setIndexingRun(false);
        entityService.setIndexingStop(true);
        return new ResponseTrue("true");
    }

    public Object indexingPage(String url, Site siteOfPage) throws IOException,
            SQLException, ParserConfigurationException {
        if (!(entityService.findSiteByName(siteOfPage) == null)) {
            siteOfPage.setId(entityService.findSiteByName(siteOfPage).getId());
        }
        siteOfPage.setStatusTime(new Timestamp(System.currentTimeMillis()));
        Site siteInDB = entityService.updateSite(siteOfPage, StatusIndexing.INDEXING);
        int pageId = 0;
        Page page = entityService.getPageByPath(url.substring(siteInDB.getUrl().length()), siteInDB);
        if (!(page == null)) {
            pageId = page.getId();
            entityService.updateLemma(pageId);
            entityService.deleteLemmaOfNullFrequency();
            entityService.deleteIndexOfPage(page);

        }
        Document doc = getDocumentPage(url, siteInDB);
        entityService.addEntitiesToDB(doc, url, codeResponse, siteOfPage, pageId);
        entityService.updateSite(siteOfPage, StatusIndexing.INDEXED);
        return new ResponseTrue("true");
    }

    public Document getDocumentPage(String url, Site site) throws SQLException,
            ParserConfigurationException, IOException {
        Set<String> linksSet = Collections.synchronizedSet(new HashSet<>());
        ParserLinks parserLinks = new ParserLinks(url, site, linksSet);
        parserLinks.setParserConfig(parserConfig);
        Document doc = parserLinks.getDocument(url);
        codeResponse = parserLinks.getCodeResponse();
        return doc;
    }

    @Transactional
    public List<RelevancePage> getRelevancePages(int offset, int limit) {
        pageable = PageRequest.of(offset, limit, Sort.by("sum_rank").descending());
        return indexRepository.findPagesOfRelevance(pageable);
    }

    @Transactional
    public int getCountPages() {
        return indexRepository.findCountOfPages();
    }

}
