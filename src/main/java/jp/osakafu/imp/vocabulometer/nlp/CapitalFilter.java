package jp.osakafu.imp.vocabulometer.nlp;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CapitalFilter implements UnaryOperator<List<Map.Entry<String, String>>> {
    @Override
    public List<Map.Entry<String, String>> apply(List<Map.Entry<String, String>> entries) {
        return IntStream.range(0, entries.size())
                .filter((i) -> (i == 0 || entries.get(i - 1).getValue().equals(".") || !firstLetterCapital(entries.get(i).getKey())))
                .mapToObj(entries::get)
                .collect(Collectors.toList());
    }

    private boolean firstLetterCapital(String element) {
        char firstLetter = element.charAt(0);
        return firstLetter >= 'A' && firstLetter <= 'Z';
    }
}
