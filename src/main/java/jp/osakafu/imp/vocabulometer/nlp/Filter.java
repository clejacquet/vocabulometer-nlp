package jp.osakafu.imp.vocabulometer.nlp;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class Filter implements UnaryOperator<List<Lemma>> {
    private Predicate<Lemma> predicate;

    Filter(Predicate<Lemma> predicate) {
        this.predicate = predicate;
    }

    @Override
    public List<Lemma> apply(List<Lemma> strings) {
        return strings
                .stream()
                .filter(this.predicate)
                .collect(Collectors.toList());
    }
}
