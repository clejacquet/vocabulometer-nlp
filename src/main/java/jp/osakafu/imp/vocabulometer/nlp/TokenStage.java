package jp.osakafu.imp.vocabulometer.nlp;

import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class TokenStage implements UnaryOperator<List<Lemma>> {
    private Collection<String> tokens;
    private Lemma.Type type;

    TokenStage(Collection<String> tokens, Lemma.Type type) {
        this.tokens = tokens;
        this.type = type;
    }

    @Override
    public List<Lemma> apply(List<Lemma> words) {
        return words
                .stream()
                .peek(w -> w.type = (tokens.contains(w.lemma.toLowerCase())) ? this.type : w.type)
                .collect(Collectors.toList());
    }
}
