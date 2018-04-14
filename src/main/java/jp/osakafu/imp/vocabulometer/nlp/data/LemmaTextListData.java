package jp.osakafu.imp.vocabulometer.nlp.data;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LemmaTextListData {
    public LemmaListData[] texts;

    public LemmaTextListData() {

    }

    public List<String> toList() {
        return Arrays.stream(this.texts)
                .map(LemmaListData::toList)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}
