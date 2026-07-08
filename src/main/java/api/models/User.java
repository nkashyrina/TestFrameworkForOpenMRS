package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String uuid;
    private String display;
    private String username;
    private String systemId;
    private UserProperties userProperties;
    private PersonForSessionResponse person;
    private List<Object> privileges;
    private List<Role> roles;
    private List<Link> links;
    private String resourceVersion;
}
