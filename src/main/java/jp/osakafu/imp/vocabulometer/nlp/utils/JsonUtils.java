package jp.osakafu.imp.vocabulometer.nlp.utils;

import javax.json.*;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class JsonUtils {
    private static <T> JsonArray toJsonArray(Collection<T> data, BiFunction<JsonArrayBuilder, T, JsonArrayBuilder> appender) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for (T item : data) {
            arrayBuilder = appender.apply(arrayBuilder, item);
        }
        return arrayBuilder.build();
    }

    public static <T> JsonArray toJsonValuesArray(Collection<T> data, Function<T, ? extends JsonValue> converter) {
        return toJsonArray(data, (builder, item) -> builder.add(converter.apply(item)));
    }

    public static <T> JsonArray toJsonStringArray(Collection<T> data, Function<T, String> converter) {
        return toJsonArray(data, (builder, item) -> builder.add(converter.apply(item)));
    }
}
