package jp.osakafu.imp.vocabulometer.nlp;

import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

abstract class RegexFilter implements UnaryOperator<List<Map.Entry<String, String>>> {
    private EasyRegex easyRegex;

    void setRegex(String pattern) {
        this.easyRegex = new EasyRegex(pattern);
    }

    @Override
    public List<Map.Entry<String, String>> apply(List<Map.Entry<String, String>> words) {
        return words
                .stream()
                .filter(w -> this.apply(w.getValue()))
                .collect(Collectors.toList());
    }

    private boolean apply(String value) {
        if (this.easyRegex == null) {
            return true;
        }

        return this.applyMatchPredicate(value);
    }

    protected EasyRegex getRegex() {
        return this.easyRegex;
    }

    protected abstract boolean applyMatchPredicate(String value);

    static RegexFilter buildNotMatch(String pattern) {
        RegexFilter filter = new NotMatchRegexFilter();
        filter.setRegex(pattern);
        return filter;
    }

    static RegexFilter buildMatch(String pattern) {
        RegexFilter filter = new MatchRegexFilter();
        filter.setRegex(pattern);
        return filter;
    }
}
