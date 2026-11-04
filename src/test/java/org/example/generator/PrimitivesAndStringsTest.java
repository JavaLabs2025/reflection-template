package org.example.generator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrimitivesAndStringsTest {
    private final Generator generator = new Generator(new Random(42));

    static Stream<Arguments> primitiveTypes() {
        return Stream.of(
                Arguments.of(boolean.class, Boolean.class),
                Arguments.of(byte.class, Byte.class),
                Arguments.of(short.class, Short.class),
                Arguments.of(char.class, Character.class),
                Arguments.of(int.class, Integer.class),
                Arguments.of(long.class, Long.class),
                Arguments.of(float.class, Float.class),
                Arguments.of(double.class, Double.class),
                Arguments.of(Boolean.class, Boolean.class),
                Arguments.of(Byte.class, Byte.class),
                Arguments.of(Short.class, Short.class),
                Arguments.of(Character.class, Character.class),
                Arguments.of(Integer.class, Integer.class),
                Arguments.of(Long.class, Long.class),
                Arguments.of(Float.class, Float.class),
                Arguments.of(Double.class, Double.class)
        );
    }

    @ParameterizedTest
    @MethodSource("primitiveTypes")
    void generatesPrimitivesAndWrappers(Class<?> requested, Class<?> expected) {
        assertInstanceOf(expected, generator.generateValueOfType(requested));
    }

    @Test
    void intValuesAreRandomAndWithinBounds() {
        List<Integer> values = Samples.of(generator, int.class);
        assertTrue(new HashSet<>(values).size() > Samples.RUNS / 2);
        values.forEach(v -> assertTrue(Math.abs(v) <= Generator.NUMBER_BOUND));
    }

    @Test
    void doubleValuesAreRandomAndWithinBounds() {
        List<Double> values = Samples.of(generator, Double.class);
        assertTrue(new HashSet<>(values).size() > Samples.RUNS / 2);
        values.forEach(v -> assertTrue(Math.abs(v) <= Generator.NUMBER_BOUND));
    }

    @Test
    void booleanTakesBothValues() {
        assertEquals(Set.of(true, false), new HashSet<>(Samples.of(generator, boolean.class)));
    }

    @Test
    void charIsLowercaseLatinLetter() {
        Samples.of(generator, char.class).forEach(c -> assertTrue(c >= 'a' && c <= 'z'));
    }

    @Test
    void stringIsNonEmptyLowercaseWordOfBoundedLength() {
        List<String> values = Samples.of(generator, String.class);
        values.forEach(s -> assertTrue(s.matches("[a-z]{1," + Generator.MAX_STRING_LENGTH + "}"), s));
        assertTrue(new HashSet<>(values).size() > 1);
    }

    @ParameterizedTest
    @ValueSource(classes = {void.class, Void.class})
    void voidIsRejected(Class<?> type) {
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(type));
    }
}
