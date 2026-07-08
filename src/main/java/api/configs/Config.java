package api.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Config {
    public static final String API_BASE_URL_CONST = "apiBaseUrl";
    public static final String API_VERSION_CONST = "apiVersion";
    public static final String API_FULL_PREFIX_CONST = "apiFullPrefix";
    public static final String ADMIN_TOKEN_CONST = "admin.token";
    public static final String ADMIN_USERNAME_CONST = "admin.username";
    public static final String ADMIN_PASSWORD_CONST = "admin.password";
    public static final String UI_BASE_URL_CONST = "uiBaseUrl";
    public static final String BROWSER_CONST = "browser";
    public static final String BROWSER_SIZE_CONST = "browserSize";
    public static final String BROWSER_REMOTE_CONST = "browserRemote";

    private static final Config INSTANCE = new Config();
    private final Properties properties = new Properties();

    private Config() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in resources");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getProperty(String key) {
        String systemValue = System.getProperty(key);

        if (systemValue != null) {
            return systemValue;
        }

        String envKey = key.toUpperCase().replaceAll("\\.", "_");
        String envValue = System.getenv(envKey);
        if (envValue != null) {
            return envValue;
        }

        return INSTANCE.properties.getProperty(key);
    }
}
