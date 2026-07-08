package common.extensions;

import api.models.roles.AdminLogin;
import common.annotations.AdminSession;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.BasePage;

public class AdminSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AdminSession annotation = context.getRequiredTestMethod().getAnnotation(AdminSession.class);
        if (annotation != null) {
            BasePage.authAsUser(AdminLogin.getAdmin());
        }
    }
}
