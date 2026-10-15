package org.example.generator.simple;

import org.example.generator.Generator;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StringGeneratorTest {

    private final Generator generator = new Generator(new Random(42));

    @Test
    void stringsUseAlphabetAndLengthLimits() {
        for (int i = 0; i < 100; i++) {
            String text = generator.generate(String.class);
            assertTrue(text.length() >= 1 && text.length() <= StringGenerator.MAX_LENGTH, text);
            assertTrue(text.chars().allMatch(c -> StringGenerator.ALPHABET.indexOf(c) >= 0), text);
        }
    }
}
