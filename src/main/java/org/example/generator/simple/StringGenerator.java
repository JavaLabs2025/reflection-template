package org.example.generator.simple;

import org.example.generator.GenerationContext;
import org.example.generator.TypeGenerator;

import java.lang.reflect.Type;
import java.util.Random;

public class StringGenerator implements TypeGenerator {

    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final int MAX_LENGTH = 10;

    @Override
    public boolean supports(Class<?> type) {
        return type == String.class;
    }

    @Override
    public String generate(Type type, GenerationContext context) {
        Random random = context.random();
        int length = random.nextInt(1, MAX_LENGTH + 1);
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }
}
