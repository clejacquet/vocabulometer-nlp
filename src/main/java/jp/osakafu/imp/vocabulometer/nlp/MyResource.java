package jp.osakafu.imp.vocabulometer.nlp;

import jp.osakafu.imp.vocabulometer.nlp.data.TextList;
import jp.osakafu.imp.vocabulometer.nlp.utils.JsonUtils;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Root resource (exposed at "lemmatize" path)
 */
@Path("/lemmatize")
public class MyResource {
    private List<String> splitByWords(String text, List<String> words) {
        List<Integer> indexesBegin = new ArrayList<>();
        List<Integer> indexesEnd = new ArrayList<>();

        indexesBegin.add(0);
        int lastIndex = 0;

        for (String word : words) {
            int indexEnd = text.indexOf(word, lastIndex);
            int indexBegin = indexEnd + word.length();

            indexesEnd.add(indexEnd);
            indexesBegin.add(indexBegin);

            lastIndex = indexBegin;
        }

        indexesEnd.add(text.length());

        List<String> result = new ArrayList<>();

        for (int i = 0; i < indexesBegin.size(); i++) {
            int indexBegin = indexesBegin.get(i);
            int indexEnd = indexesEnd.get(i);
            result.add(text.substring(indexBegin, indexEnd));
        }

        return result;
    }

    private JsonObject textToJson(Lemmatizer lemmatizer, String p) {
        List<Map.Entry<String, String>> lemmas = lemmatizer.lemmatize(p);

        lemmas.forEach((entry) -> System.out.println(entry.getKey() + ": " + entry.getValue()));

        // Get the list of words -- no punctuation
        TokenFilter punctuationFilter = new TokenFilter(ModelProvider.getPunctuation());
        List<String> words = punctuationFilter
                .apply(lemmas)
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Filter the list of (word, lemma) pair to only vocabulary lemmas
        FilterPipeline pipeline = new FilterPipeline();
        pipeline.add(
                new CapitalFilter(), // No capital word after a point
                punctuationFilter,  // No punctuation
                new TokenFilter(ModelProvider.getStopWords()), // No stop word
                new NERFilter(p, ModelProvider.getNerClassifier()), // No organization name, location or time
                RegexFilter.buildNotMatch("^..?$", true), // No word less than two characters
                RegexFilter.buildNotMatch("\\d", true), // No word with digits
                RegexFilter.buildNotMatch("[\\[\\]{}@\\\\/\"'()`\\-]", true) // No special characters
        );
        lemmas = pipeline
                .apply(lemmas);

        Map<String, String> wordToLemma = lemmas
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (first, second) -> first));

        // Build the final JSON object for the given text
        return Json.createObjectBuilder()
                .add("text", p)
                .add("interWords", JsonUtils.toJsonStringArray(
                        splitByWords(p, words),
                        Function.identity()))
                .add("words", JsonUtils.toJsonValuesArray(
                        words,
                        word -> {
                            if (wordToLemma.containsKey(word)) {
                                return Json.createObjectBuilder()
                                        .add("raw", word)
                                        .add("lemma", wordToLemma.get(word).toLowerCase())
                                        .build();
                            } else {
                                return Json.createObjectBuilder()
                                        .add("raw", word)
                                        .build();
                            }

                        }))
                .build();
    }

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String getIt(TextList input) {
        try {
            final Lemmatizer lemmatizer = new Lemmatizer(ModelProvider.getPosPipeline());

            JsonArray result = JsonUtils.toJsonValuesArray(Arrays
                    .stream(input.texts)
                    .map(text -> {
                        List<JsonObject> paragraphJson = Arrays.stream(text.split("\n"))
                                .filter(p -> !p.isEmpty())
                                .map(p -> this.textToJson(lemmatizer, p))
                                .collect(Collectors.toList());

                        return Json.createObjectBuilder()
                                .add("result", JsonUtils.toJsonValuesArray(paragraphJson, Function.identity()))
                                .build();
                    })
                    .collect(Collectors.toList()), Function.identity());

            return Json.createObjectBuilder().add("texts", result).build().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
