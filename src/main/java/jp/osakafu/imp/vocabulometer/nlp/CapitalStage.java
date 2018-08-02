package jp.osakafu.imp.vocabulometer.nlp;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CapitalStage implements UnaryOperator<List<Lemma>> {
    @Override
    public List<Lemma> apply(List<Lemma> entries) {
        return IntStream.range(0, entries.size())
                .peek((i) -> {
                    if (!(i == 0 || entries.get(i - 1).lemma.equals(".") || !firstLetterCapital(entries.get(i).raw))) {
                        entries.get(i).type = Lemma.Type.ENTITY;
                    }
                })
                .mapToObj(entries::get)
                .collect(Collectors.toList());
    }

    private boolean firstLetterCapital(String element) {
        char firstLetter = element.charAt(0);
        return firstLetter >= 'A' && firstLetter <= 'Z';
    }
}
