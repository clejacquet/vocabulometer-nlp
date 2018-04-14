package jp.osakafu.imp.vocabulometer.nlp;

public class NotMatchRegexFilter extends RegexFilter {
    @Override
    protected boolean applyMatchPredicate(String value) {
        return super.getRegex().not_match(value);
    }
}
