package jp.osakafu.imp.vocabulometer.nlp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class EasyRegex {
    private Pattern regex;

    EasyRegex(String regex) {
        this.regex = Pattern.compile(regex);
    }

    boolean match(String input) {
        Matcher matcher = this.regex.matcher(input);
        return matcher.find();
    }

    boolean not_match(String input) {
        return !this.match(input);
    }
}
