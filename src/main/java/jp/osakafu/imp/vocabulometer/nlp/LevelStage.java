package jp.osakafu.imp.vocabulometer.nlp;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class LevelStage implements UnaryOperator<List<Lemma>> {
    @Override
    public List<Lemma> apply(List<Lemma> lemmas) {
        Map<String, Integer> levels = ModelProvider.getVocab().levels;
        return lemmas
                .stream()
                .peek(lemma -> {
                    if (levels.containsKey(lemma.lemma)) {
                        lemma.level = levels.get(lemma.lemma);
                        lemma.type = Lemma.Type.VOCAB;
                    } else {
                        lemma.level = 0;
                    }
                } )
                .collect(Collectors.toList());
    }
}
