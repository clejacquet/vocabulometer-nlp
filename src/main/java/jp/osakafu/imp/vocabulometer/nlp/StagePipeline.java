package jp.osakafu.imp.vocabulometer.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

class StagePipeline {
    private List<UnaryOperator<List<Lemma>>> filters = new ArrayList<>();

    @SafeVarargs
    final void add(UnaryOperator<List<Lemma>>... filters) {
        this.filters.addAll(Arrays.asList(filters));
    }

    List<Lemma> apply(List<Lemma> words) {
        for (UnaryOperator<List<Lemma>> filter : this.filters) {
            words = filter.apply(words);
        }

        return words;
    }
}
