package jp.osakafu.imp.vocabulometer.nlp;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class TokenFilter implements UnaryOperator<List<Map.Entry<String, String>>> {
    private Collection<String> tokens;
    private boolean include;

    TokenFilter(Collection<String> tokens, boolean include) {
        this.tokens = tokens;
        this.include = include;
    }

    @Override
    public List<Map.Entry<String, String>> apply(List<Map.Entry<String, String>> words) {
        return words
                .stream()
                .filter(w -> tokens.contains(w.getValue().toLowerCase()) ^ !include)
                .collect(Collectors.toList());
    }

    public static TokenFilter buildInclude(Collection<String> tokens) {
        return new TokenFilter(tokens, true);
    }

    public static TokenFilter buildNotInclude(Collection<String> tokens) {
        return new TokenFilter(tokens, false);
    }
}
