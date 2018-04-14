package jp.osakafu.imp.vocabulometer.nlp;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.SentenceUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class NERFilter implements UnaryOperator<List<Map.Entry<String, String>>> {
    private String documentText;
    private AbstractSequenceClassifier<CoreLabel> classifier;

    NERFilter(String documentText, AbstractSequenceClassifier<CoreLabel> classifier) {
        this.documentText = documentText;
        this.classifier = classifier;
    }

    @Override
    public List<Map.Entry<String, String>> apply(List<Map.Entry<String, String>> words) {
        try {
            List<List<CoreLabel>> out = classifier.classify(documentText);

            Set<String> entities = out
                    .stream()
                    .flatMap(List::stream)
                    .filter(w -> !w.get(CoreAnnotations.AnswerAnnotation.class).equals("O"))
                    .map(CoreLabel::word)
                    .collect(Collectors.toCollection(HashSet::new));

            return words
                    .stream()
                    .filter(w -> !entities.contains(w.getKey()))
                    .collect(Collectors.toList());

        } catch (Exception | Error e) {
            e.printStackTrace();
            System.out.println(e);
            return null;
        }
    }
}
