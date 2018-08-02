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

class NERStage implements UnaryOperator<List<Lemma>> {
    private List<List<CoreLabel>> output;

    NERStage(String documentText, AbstractSequenceClassifier<CoreLabel> classifier) {
        this.output = classifier.classify(documentText);
    }

    @Override
    public List<Lemma> apply(List<Lemma> words) {
        try {
            Set<String> entities = this.output
                    .stream()
                    .flatMap(List::stream)
                    .filter(w -> !w.get(CoreAnnotations.AnswerAnnotation.class).equals("O"))
                    .map(CoreLabel::word)
                    .collect(Collectors.toCollection(HashSet::new));

            return words
                    .stream()
                    .peek(w -> w.type = (entities.contains(w.raw)) ? Lemma.Type.ENTITY : w.type)
                    .collect(Collectors.toList());

        } catch (Exception | Error e) {
            e.printStackTrace();
            System.out.println(e);
            return null;
        }
    }
}
