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
        List<Lemma> lemmas = lemmatizer.lemmatize(p);

        // Get the list of words without punctuation
        TokenStage punctuationStage = new TokenStage(ModelProvider.getPunctuation(), Lemma.Type.PUNCTUATION);
        List<Lemma> words = punctuationStage
                .apply(lemmas)
                .stream()
                .filter(lemma -> lemma.type != Lemma.Type.PUNCTUATION)
                .collect(Collectors.toList());

        // Filter the list of (word, lemma) pair to only vocabulary lemmas
        StagePipeline pipeline = new StagePipeline();
        pipeline.add(
                new RegexStage("[^a-z-]", false, Lemma.Type.UNKNOWN),
                new RegexStage("[(){}\\[\\]]", false, Lemma.Type.UNKNOWN),
                new RegexStage("^-?[0-9,\\.]+$", false, Lemma.Type.NUMBER),
                new CapitalStage(), // No capital word after a point
                punctuationStage,  // No punctuation
                new NERStage(p, ModelProvider.getNerClassifier()), // No organization name, location or time
                new LevelStage(),
                new TokenStage(ModelProvider.getStopWords(), Lemma.Type.STOPWORD) // Not a stopword
        );

        lemmas = pipeline.apply(lemmas);

//        for (Lemma lemma : lemmas
//                .stream()
//                .filter(lemma -> lemma.type == Lemma.Type.VOCAB || lemma.type == Lemma.Type.DEFAULT)
//                .collect(Collectors.toList())) {
//            System.out.println(lemma.raw + " / " + lemma.lemma + " / " + lemma.type);
//        }

        int vocabDefaultSize = lemmas
                .stream()
                .filter(lemma -> lemma.type == Lemma.Type.VOCAB || lemma.type == Lemma.Type.DEFAULT)
                .collect(Collectors.toSet())
                .size();

        double unrecognizedRate = 0.0;

        if (vocabDefaultSize != 0) {
            unrecognizedRate = 1.0 -
                    (double) lemmas
                            .stream()
                            .filter(lemma -> lemma.type == Lemma.Type.VOCAB)
                            .collect(Collectors.toSet())
                            .size()
                            / (double) vocabDefaultSize;
        }

        System.out.println(unrecognizedRate);

//        for (Lemma lemma : lemmas) {
//            System.out.println(lemma.raw + ": " + lemma.type.toString() + "(" + lemma.type + ")");
//        }

        // Build the final JSON object for the given text
        return Json.createObjectBuilder()
                .add("text", p)
                .add("interWords", JsonUtils.toJsonStringArray(
                        splitByWords(p, words
                                .stream()
                                .map(Lemma::getRaw)
                                .collect(Collectors.toList())),
                        Function.identity()))
                .add("words", JsonUtils.toJsonValuesArray(words, Lemma::toJson))
                .add("unrecognized", JsonUtils.toJsonStringArray(
                        lemmas
                                .stream()
                                .filter(lemma -> lemma.type == Lemma.Type.DEFAULT)
                                .collect(Collectors.toSet()),
                        Lemma::getLemma))
                .add("unrecognizedRate", unrecognizedRate)
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
