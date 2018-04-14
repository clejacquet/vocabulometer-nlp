package jp.osakafu.imp.vocabulometer.nlp;

import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

class Filter implements UnaryOperator<Map<String, String>> {
    private Predicate<Map.Entry<String, String>> predicate;

    Filter(Predicate<Map.Entry<String, String>> predicate) {
        this.predicate = predicate;
    }

    @Override
    public Map<String, String> apply(Map<String, String> strings) {
        return strings
                .entrySet()
                .stream()
                .filter(this.predicate)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
