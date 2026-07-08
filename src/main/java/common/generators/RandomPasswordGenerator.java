package common.generators;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// random password generator that should be minimum: 8 symbols, contain at least one number, Capital letter and special symbol
public class RandomPasswordGenerator {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{};:,.<>?";
    private static final String ALL = UPPERCASE + LOWERCASE + DIGITS + SPECIAL;

    private static final int MIN_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();

    public static String generate() {
        return generate(MIN_LENGTH);
    }

    public static String generate(int length) {
        if (length < MIN_LENGTH) {
            throw new IllegalArgumentException(
                    "Password length must be at least " + MIN_LENGTH + " characters");
        }

        List<Character> chars = new ArrayList<>(length);

        chars.add(randomCharFrom(UPPERCASE));
        chars.add(randomCharFrom(DIGITS));
        chars.add(randomCharFrom(SPECIAL));
        chars.add(randomCharFrom(LOWERCASE));

        for (int i = chars.size(); i < length; i++) {
            chars.add(randomCharFrom(ALL));
        }

        Collections.shuffle(chars, random);

        StringBuilder password = new StringBuilder(length);
        chars.forEach(password::append);
        return password.toString();
    }

    private static char randomCharFrom(String source) {
        return source.charAt(random.nextInt(source.length()));
    }
}
