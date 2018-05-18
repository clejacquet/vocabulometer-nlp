package jp.osakafu.imp.vocabulometer.nlp;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

class Lemmatizer {
    private StanfordCoreNLP pipeline;

    Lemmatizer(StanfordCoreNLP pipeline) {
        // StanfordCoreNLP loads a lot of models, so you probably
        // only want to do this once per execution
        this.pipeline = pipeline;
    }

    List<Map.Entry<String, String>> lemmatize(String documentText) {
        List<Map.Entry<String, String>> lemmas = new ArrayList<>();

        // create an empty Annotation just with the given text
        Annotation document = new Annotation(documentText);

        // run all Annotators on this text
        this.pipeline.annotate(document);

        // Iterate over all of the sentences found
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for(CoreMap sentence: sentences) {
            // Iterate over all tokens in a sentence
            for (CoreLabel token: sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);

                // Retrieve and add the lemma for each word into the list of lemmas
                lemmas.add(new AbstractMap.SimpleEntry<>(token.originalText(), lemma));
            }
        }

        return lemmas;
    }
}
