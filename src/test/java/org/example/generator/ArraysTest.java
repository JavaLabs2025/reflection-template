package org.example.generator;

import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.testclasses.Color;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArraysTest {
    private final Generator generator = new Generator(new Random(42));

    @Test
    void primitiveArrayHasBoundedNonZeroLength() {
        for (int[] array : Samples.of(generator, int[].class)) {
            assertTrue(array.length >= 1 && array.length <= Generator.MAX_SIZE);
        }
    }

    @Test
    void stringArrayContainsStrings() {
        String[] array = generator.generateValueOfType(String[].class);
        assertTrue(array.length > 0);
        Arrays.stream(array).forEach(s -> assertFalse(s.isEmpty()));
    }

    @Test
    void enumArrayContainsConstants() {
        Color[] array = generator.generateValueOfType(Color[].class);
        assertTrue(array.length > 0);
        Arrays.stream(array).forEach(color -> assertNotNull(color));
    }

    @Test
    void userClassArrayContainsObjects() {
        Rectangle[] array = generator.generateValueOfType(Rectangle[].class);
        assertTrue(array.length > 0);
        Arrays.stream(array).forEach(r -> assertTrue(r.getArea() > 0));
    }

    @Test
    void interfaceArrayContainsImplementations() {
        Shape[] array = generator.generateValueOfType(Shape[].class);
        assertTrue(array.length > 0);
        Arrays.stream(array).forEach(s -> assertInstanceOf(Shape.class, s));
    }

    @Test
    void multiDimensionalArray() {
        int[][] array = generator.generateValueOfType(int[][].class);
        assertTrue(array.length > 0);
        Arrays.stream(array).forEach(inner -> assertTrue(inner.length > 0));
    }

    @Test
    void genericArray() {
        Object value = generator.generateValueOfType(new TypeRef<List<String>[]>() {}.type);
        List<?>[] array = assertInstanceOf(List[].class, value);
        assertTrue(array.length > 0);
        for (List<?> list : array) {
            list.forEach(element -> assertInstanceOf(String.class, element));
        }
    }
}
