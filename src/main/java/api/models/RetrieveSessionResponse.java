package api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveSessionResponse extends BaseModel {
    private boolean authenticated;
    private String locale;
    private List<String> allowedLocales;
    private User user;
    private String sessionLocation;
    private CurrentProvider currentProvider;
}
