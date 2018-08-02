package jp.osakafu.imp.vocabulometer.nlp;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class RegexStage implements UnaryOperator<List<Lemma>> {
    private EasyRegex easyRegex;
    private Lemma.Type type;
    private boolean useLemma;

    RegexStage(String pattern, boolean useLemma, Lemma.Type type) {
        this.easyRegex = new EasyRegex(pattern);
        this.type = type;
        this.useLemma = useLemma;
    }

    @Override
    public List<Lemma> apply(List<Lemma> words) {
        return words
                .stream()
                .peek(this::apply)
                .collect(Collectors.toList());
    }

    private void apply(Lemma word) {
        if (this.easyRegex != null && this.easyRegex.match((this.useLemma) ? word.lemma : word.raw)) {
            word.type = this.type;
        }
    }

    protected EasyRegex getRegex() {
        return this.easyRegex;
    }
}
