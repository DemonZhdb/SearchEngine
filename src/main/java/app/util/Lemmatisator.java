package app.util;

import app.util.stemmer.RussianStemmer;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class Lemmatisator {

    private HashMap<String, Integer> wordsMap;

    private final String regexpWord = "[а-яА-ЯёЁ]+";

    private final String regexpText = "\\s*(\\s|\\?|\\||»|«|\\*|,|!|\\.)\\s*";
    private final LuceneMorphology luceneMorph = new RussianLuceneMorphology();

    public Lemmatisator() throws IOException {
    }

    public HashMap<String, Integer> textToLemma(String sourceText) {
        wordsMap = new HashMap<>();
        String text = sourceText.trim();

        String[] words = text.toLowerCase().split(regexpText);

        for (String word : words) {
            if (wordCheck(word)) {
                List<String> wordBaseForms = luceneMorph.getNormalForms(word);
                wordBaseForms.forEach(w -> {
                    wordsMap.put(w, wordsMap.getOrDefault(w, 0) + 1);
                });
            }
        }
        return wordsMap;
    }

    public boolean wordCheck(String word) {

        if (word.matches(regexpWord)) {
            List<String> wordBaseForms =
                    luceneMorph.getMorphInfo(word);
            if ((!wordBaseForms.get(0).endsWith("ПРЕДЛ") && (!wordBaseForms.get(0).endsWith("СОЮЗ")) &&
                    (!wordBaseForms.get(0).endsWith("ЧАСТ")) && (!wordBaseForms.get(0).endsWith("МЕЖД")))) {
                return true;
            }
        }
        return false;
    }

    public String getSnippet(String content, String textQuery) {

        String textForSearchQuery = Jsoup.parse(content).getElementsContainingOwnText(textQuery).text();
        Pattern pattern = Pattern.compile(textQuery);
        Matcher matcher = pattern.matcher(textForSearchQuery);
        String snippet ;
        if (matcher.find()) {
            int beginIndex = matcher.start() > 80 ?
                    textForSearchQuery.lastIndexOf(' ', matcher.start() - 60) : 0;
            int endIndex = Math.min(beginIndex + matcher.end() + 160, textForSearchQuery.length());
            snippet = textForSearchQuery.substring(beginIndex, endIndex);
            snippet = StringUtils.replace(snippet, textQuery, "<b>" + textQuery + "</b>");

        } else {
            textQuery = textQuery.trim();
            String[] words = textQuery.toLowerCase().split(regexpText);

            StringBuilder builderSnippet = new StringBuilder();
            RussianStemmer russianStemmer = new RussianStemmer();

            for (String word : words) {
                if (wordCheck(word)) {
                    russianStemmer.setCurrent(word);
                    if (russianStemmer.stem()) {
                        word = russianStemmer.getCurrent() + ".*?\\b";
                    }
                    String textForSearchWord = Jsoup.parse(content).getElementsMatchingOwnText(word).text();
                    pattern = Pattern.compile(word, Pattern.CASE_INSENSITIVE);
                    matcher = pattern.matcher(textForSearchWord);
                    if (matcher.find()) {
                        int beginIndex = matcher.start() > 35 ?
                                textForSearchWord.lastIndexOf(' ', matcher.start() - 15) : 0;
                        int endIndex = Math.min(matcher.start() + 80, textForSearchWord.length());
                        String result = matcher.group();
                        String snippetWord = textForSearchWord.substring(beginIndex, endIndex);
                        snippetWord = StringUtils.replace(snippetWord, result, "<b>" + result + "</b>");
                        builderSnippet.append(snippetWord).append("...");
                    }
                }
            }
            snippet = builderSnippet.toString();
        }
        return snippet;
    }

}
