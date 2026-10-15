package org.example.generator.collection;

import org.example.classes.Rectangle;
import org.example.generator.GenerationContext;
import org.example.generator.Generator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ArrayGeneratorTest {

    private final Generator generator = new Generator(new Random(42));

    @Test
    void generatesPrimitiveArray() {
        int[] numbers = generator.generate(int[].class);
        assertTrue(numbers.length >= 1 && numbers.length <= GenerationContext.MAX_SIZE);
    }

    @Test
    void generatesStringArray() {
        String[] texts = generator.generate(String[].class);
        assertTrue(texts.length > 0);
        assertTrue(Arrays.stream(texts).allMatch(Objects::nonNull));
    }

    @Test
    void generatesObjectArray() {
        Rectangle[] rectangles = generator.generate(Rectangle[].class);
        assertTrue(rectangles.length > 0);
        assertTrue(Arrays.stream(rectangles).allMatch(Objects::nonNull));
    }

    @Test
    void generatesMultidimensionalArray() {
        double[][] matrix = generator.generate(double[][].class);
        assertTrue(matrix.length > 0);
        assertTrue(Arrays.stream(matrix).allMatch(row -> row.length > 0));
    }
}
