package common.extensions;

import api.models.roles.AdminLogin;
import common.annotations.InjectAdmin;
import org.junit.jupiter.api.extension.*;

public class InjectAdminExtension implements ParameterResolver {
    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType() == AdminLogin.class
                && parameterContext.isAnnotated(InjectAdmin.class);
    }

    @Override
    public AdminLogin resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AdminLogin.getAdmin();
    }
}
