package common.extensions;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.HashMap;
import java.util.Map;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private final ThreadLocal<Map<String, Long>> startTestTimes = ThreadLocal.withInitial(HashMap::new);

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        String testName = context.getRequiredTestClass().getPackageName() + "." + context.getDisplayName();
        startTestTimes.get().put(testName, System.currentTimeMillis());
        System.out.println("Thread " + Thread.currentThread().getName() + ": Test started " + testName);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        String testName = context.getRequiredTestClass().getPackageName() + "." + context.getDisplayName();
        long testDuration = System.currentTimeMillis() - startTestTimes.get().get(testName);
        System.out.println("Thread " + Thread.currentThread().getName() + ": Test finished " + testName +
                ", test duration: " + testDuration + "ms");
    }
}
