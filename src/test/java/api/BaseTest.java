package api;

import common.annotations.AutoCleanup;
import common.extensions.CleanupExtension;
import common.extensions.SkipExtension;
import common.extensions.TimingExtension;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(CleanupExtension.class)
@ExtendWith(SkipExtension.class)
@ExtendWith(TimingExtension.class)
@AutoCleanup
public class BaseTest {
    protected SoftAssertions softly;

    @BeforeEach
    public void setupTest() {
        this.softly = new SoftAssertions();
    }

    @AfterEach
    public void afterTest() {
        softly.assertAll();
    }
}
