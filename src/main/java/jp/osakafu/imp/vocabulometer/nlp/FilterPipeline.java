package jp.osakafu.imp.vocabulometer.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

class FilterPipeline {
    private List<UnaryOperator<List<Map.Entry<String, String>>>> filters = new ArrayList<>();

    @SafeVarargs
    final void add(UnaryOperator<List<Map.Entry<String, String>>>... filters) {
        this.filters.addAll(Arrays.asList(filters));
    }

    List<Map.Entry<String, String>> apply(List<Map.Entry<String, String>> words) {
        for (UnaryOperator<List<Map.Entry<String, String>>> filter : this.filters) {
            words = filter.apply(words);
        }

        return words;
    }
}
