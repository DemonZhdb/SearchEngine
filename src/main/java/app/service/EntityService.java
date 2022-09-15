package app.service;


import app.model.*;
import app.repository.*;
import app.util.Lemmatisator;
import lombok.Data;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Service
public class EntityService {

    //    private JdbcTemplate jdbcTemplate;
    private boolean indexingRun;
    private boolean indexingStop;

    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private IndexRepository indexRepository;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private LemmaRepository lemmaRepository;
    @Autowired
    private FieldRepository fieldRepository;
    @Autowired
    private QueryLemmaRepository queryLemmaRepository;

    @Transactional
    public void deleteAllPages() {
        pageRepository.deleteAll();
    }

    @Transactional
    public void deleteAllLemmas() {
        lemmaRepository.deleteAll();
    }

    @Transactional
    public void deleteAllQueryLemma() {
        queryLemmaRepository.deleteAll();
    }

    @Transactional
    public Site updateSite(Site site, StatusIndexing status) {
        site.setStatus(status);
        site.setStatusTime(new Timestamp(System.currentTimeMillis()));
        return siteRepository.save(site);
    }

    @Transactional
    public Site updateLastError(Site site, String errorMessage) {
        site.setLastError(errorMessage);
        site.setStatusTime(new Timestamp(System.currentTimeMillis()));
        return siteRepository.save(site);
    }

    @Transactional(readOnly = true)
    public Iterable<Field> findAllFields() {
        return fieldRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Site findSiteByName(Site site) {
        return siteRepository.findByName(site.getName());
    }

    @Transactional(readOnly = true)
    public Site findSiteByUrl(String siteUrl) {
        return siteRepository.findByUrl(siteUrl);
    }

    @Transactional(readOnly = true)
    public Iterable<Site> findSites() {
        return siteRepository.findAll();
    }

    @Transactional
    public void updateLemma(int pageId) {
        lemmaRepository.updateLemmaFrequency(pageId);
    }

    @Transactional
    public void deleteLemmaOfNullFrequency() {
        lemmaRepository.deleteOfFrequencyIsNull();
    }

    @Transactional
    public void deleteLemmasOfSite(Site site) {
        lemmaRepository.deleteBySiteByLemma(site);
    }

    @Transactional
    public void deletePagesOfSite(Site site) {
        pageRepository.deleteBySiteByPage(site);
    }

    @Transactional
    public void deleteIndexOfPage(Page page) {
        indexRepository.deleteByPageByIndex(page);
    }

    @Transactional
    public void deleteIndexOfSite(Site site) {
        Iterable<Page> pages = pageRepository.findBySiteByPage(site);
        indexRepository.deleteByPageByIndexIn(pages);
    }

    @Transactional(readOnly = true)
    public List<Lemma> listLemmaOrderByFrequency(HashMap<String, Integer> lemmaMap, Site site) {
        List<String> lemmas = new ArrayList<>(lemmaMap.keySet());
        return lemmaRepository.findByLemmaInAndSiteByLemmaOrderByFrequency(lemmas, site);
    }

    @Transactional(readOnly = true)
    public Page getPageByPath(String path, Site site) {
        return pageRepository.findByPathAndSiteByPage(path, site);
    }

    @Transactional
    public void addQueryLemma(String lemma, int lemmaId) {
        QueryLemma queryLemma = new QueryLemma();
        queryLemma.setLemma(lemma);
        queryLemma.setLemmaId(lemmaId);
        queryLemmaRepository.save(queryLemma);
    }

    @Transactional(readOnly = true)
    public Lemma getLemmaById(int lemmaId, Site site) {
        return lemmaRepository.findByIdAndSiteByLemma(lemmaId, site);
    }

    @Transactional(readOnly = true)
    public Lemma getLemmaByName(String lemma, Site site) {
        return lemmaRepository.findByLemmaAndSiteByLemma(lemma, site);
    }

    public void addEntitiesToDB(Document doc, String url, int code, Site site, int pageId) throws SQLException, IOException {
        String path = url.substring(url.indexOf('/', url.indexOf(".")));
        String content = "Page not found";
        if (!(doc == null)) {
            content = doc.html();
        }
        Page page = addPageToDB(path, code, content, site, pageId);
        if (code == 200) {
            Lemmatisator lemmatisator = new Lemmatisator();
            Map<String, Float> pageLemmaMap = new HashMap();
            Iterable<Field> fields = findAllFields();
            for (Field field : fields) {
                String fieldNameText = doc.select(field.getSelector()).text();
                HashMap<String, Integer> fieldNameLemmaMap = lemmatisator.textToLemma(fieldNameText);
                fieldNameLemmaMap.forEach((key, value) -> {
                    pageLemmaMap.put(key, pageLemmaMap.getOrDefault(key, 0f) + value * field.getWeight());
                });
            }
            HashMap<Integer, Float> mapId = lemmaAddToDB(pageLemmaMap, site);
            indexAddToDB(page, mapId, site);
        }
        updateSite(site, StatusIndexing.INDEXING);
    }

    @Transactional
    public synchronized Page addPageToDB(String path, int code, String content, Site site, int pageId) {
        Page page = new Page();
        if (!(pageId == 0)) {
            page.setId(pageId);
        }
        page.setPath(path);
        page.setCode(code);
        page.setContent(content);
        page.setSiteByPage(site);
        pageRepository.save(page);
        return page;
    }

    @Transactional
    public synchronized HashMap<Integer, Float> lemmaAddToDB(Map<String, Float> lemmaMap, Site site) {
        HashMap<Integer, Float> mapId = new HashMap<>();

        for (String lemma : lemmaMap.keySet()) {
            lemmaRepository.insertLemmaAndSite(lemma, site, 1);
            int idLemma = getLemmaByName(lemma, site).getId();
            mapId.put(idLemma, lemmaMap.get(lemma));
        }
        return mapId;
    }

    @Transactional
    public synchronized void indexAddToDB(Page page, HashMap<Integer, Float> idMap, Site site) {
        idMap.forEach((lemmaId, rank) -> {
            Index index = new Index();
            index.setPageByIndex(page);
            index.setLemmaByIndex(getLemmaById(lemmaId, site));
            index.setRankLemma(rank);
            indexRepository.save(index);
        });

    }

}
