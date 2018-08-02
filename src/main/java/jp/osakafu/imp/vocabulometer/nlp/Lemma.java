package jp.osakafu.imp.vocabulometer.nlp;

import javax.json.Json;
import javax.json.JsonObject;

public class Lemma {
    public enum Type {
        DEFAULT,
        UNKNOWN,
        PUNCTUATION,
        STOPWORD,
        ENTITY,
        NUMBER,
        VOCAB
    }

    public int level;
    public String lemma;
    public String raw;
    public Type type;

    public Lemma(String lemma, String raw, int level) {
        this.level = level;
        this.lemma = lemma;
        this.raw = raw;
        this.type = Type.DEFAULT;
    }

    public Lemma(String lemma, String raw) {
        this(lemma, raw, 0);
    }

    public int getLevel() {
        return this.level;
    }

    public String getLemma() {
        return this.lemma;
    }

    public String getRaw() {
        return this.raw;
    }

    public Type getType() { return this.type; }

    public JsonObject toJson() {
        if (this.type == Type.VOCAB) {
            return Json.createObjectBuilder()
                    .add("raw", this.raw)
                    .add("lemma", this.lemma.toLowerCase())
                    .add("level", this.level)
                    .build();
        } else {
            return Json.createObjectBuilder()
                    .add("raw", this.raw)
                    .build();
        }
    }

    @Override
    public int hashCode() {
        return this.lemma.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Lemma)) {
            return false;
        }

        return this.lemma.equals(((Lemma) obj).lemma);
    }
}
