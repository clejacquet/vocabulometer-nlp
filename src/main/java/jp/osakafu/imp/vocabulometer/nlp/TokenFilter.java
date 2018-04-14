package jp.osakafu.imp.vocabulometer.nlp;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class TokenFilter implements UnaryOperator<List<Map.Entry<String, String>>> {
    private Set<String> stopwords;

    TokenFilter(Set<String> stopwords) {
        this.stopwords = stopwords;
    }

    @Override
    public List<Map.Entry<String, String>> apply(List<Map.Entry<String, String>> words) {
        return words
                .stream()
                .filter(w -> !stopwords.contains(w.getValue().toLowerCase()))
                .collect(Collectors.toList());
    }
}
