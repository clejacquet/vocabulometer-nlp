package jp.osakafu.imp.vocabulometer.nlp;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

abstract class RegexFilter implements UnaryOperator<List<Map.Entry<String, String>>> {
    private EasyRegex easyRegex;
    private boolean useLemma;

    void setRegex(String pattern, boolean useLemma) {
        this.easyRegex = new EasyRegex(pattern);
        this.useLemma = useLemma;
    }

    @Override
    public List<Map.Entry<String, String>> apply(List<Map.Entry<String, String>> words) {
        return words
                .stream()
                .filter(this::apply)
                .collect(Collectors.toList());
    }

    private boolean apply(Map.Entry<String, String> word) {
        if (this.easyRegex == null) {
            return true;
        }

        return this.applyMatchPredicate(this.useLemma ? word.getValue() : word.getKey());
    }

    protected EasyRegex getRegex() {
        return this.easyRegex;
    }

    protected abstract boolean applyMatchPredicate(String value);

    static RegexFilter buildNotMatch(String pattern, boolean useLemma) {
        RegexFilter filter = new NotMatchRegexFilter();
        filter.setRegex(pattern, useLemma);
        return filter;
    }

    static RegexFilter buildMatch(String pattern, boolean useLemma) {
        RegexFilter filter = new MatchRegexFilter();
        filter.setRegex(pattern, useLemma);
        return filter;
    }
}
