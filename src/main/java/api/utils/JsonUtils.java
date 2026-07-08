package api.utils;

import api.models.ResultsWrapper;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

public final class JsonUtils {
    private JsonUtils() {

    }

    private static final ObjectMapper objectMapper = JsonMapper.builder().build();

    public static <T> List<T> extractResultsList(String jsonString, Class<T> clazz) {
        try {
            ResultsWrapper<T> wrapper = objectMapper.readValue(
                    jsonString,
                    objectMapper.getTypeFactory().constructParametricType(ResultsWrapper.class, clazz)
            );
            return wrapper.getResults();
        } catch (JacksonException e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }
}
