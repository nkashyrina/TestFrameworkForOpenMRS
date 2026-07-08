package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse extends BaseModel{
    private Error error;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Error {
        private String message;
        private String code;
        private List<Object> globalErrors;
        private Map<String, List<FieldError>> fieldErrors;
        private String detail;
        private String rawMessage;
        private String translatedMessage;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FieldError {
        private String code;
        private String message;
    }
}
