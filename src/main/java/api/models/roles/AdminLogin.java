package api.models.roles;

import api.configs.Config;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminLogin {
    String username;
    String password;

    public static AdminLogin getAdmin() {
        return AdminLogin.builder()
                .username(Config.getProperty(Config.ADMIN_USERNAME_CONST))
                .password(Config.getProperty(Config.ADMIN_PASSWORD_CONST))
                .build();
    }
}
