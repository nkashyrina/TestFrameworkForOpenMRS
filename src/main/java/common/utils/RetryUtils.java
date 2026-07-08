package common.utils;

import com.codeborne.selenide.Selenide;
import common.helpers.StepLogger;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Принимаем на вход общего ретрая:
 * 1) что повторяем
 * 2) условие выхода
 * 3) максимальное количетсво попыток
 * 4) задержка между каждой попыткой
 */
public final class RetryUtils {
    private RetryUtils() { }

    public static <T> T retry(
            String title,
            Supplier<T> action,
            Predicate<T> condition,
            int maxAttempts,
            long delayMillis) {

        T result;
        int attempts = 0;

        while (attempts < maxAttempts) {
            attempts++;

            try {
                result = StepLogger.log("Attempt" + attempts + ": " + title, () -> action.get());

                if (condition.test(result)) {
                    return result;
                }
            } catch (Throwable e) {
                System.out.println("Exception " + e.getMessage());
            }

            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("Retry failed after " + maxAttempts + " attempts");
    }

    public static <T> T retryStable(
            String title,
            Supplier<T> action,
            BiPredicate<T, T> isStable,
            int maxAttempts,
            long delayMillis
    ) {
        T previous = null;
        T current = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            current = StepLogger.log("Attempt " + attempt + ": " + title, () -> action.get());

            if (previous != null && isStable.test(previous, current)) {
                return current;
            }

            previous = current;
            Selenide.sleep(delayMillis);
        }

        throw new RuntimeException("Value did not stabilize after " + maxAttempts + " attempts");
    }
}
