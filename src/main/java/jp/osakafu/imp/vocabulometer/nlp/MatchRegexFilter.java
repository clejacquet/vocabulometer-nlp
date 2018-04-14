package jp.osakafu.imp.vocabulometer.nlp;

class MatchRegexFilter extends RegexFilter {
    @Override
    protected boolean applyMatchPredicate(String value) {
        return super.getRegex().match(value);
    }
}
