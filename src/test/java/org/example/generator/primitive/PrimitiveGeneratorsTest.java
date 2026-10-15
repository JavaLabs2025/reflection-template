package org.example.generator.primitive;

import org.example.generator.Generator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrimitiveGeneratorsTest {

    private final Generator generator = new Generator(new Random(42));

    static Stream<Arguments> primitives() {
        return Stream.of(
                Arguments.of(boolean.class, Boolean.class),
                Arguments.of(byte.class, Byte.class),
                Arguments.of(char.class, Character.class),
                Arguments.of(short.class, Short.class),
                Arguments.of(int.class, Integer.class),
                Arguments.of(long.class, Long.class),
                Arguments.of(float.class, Float.class),
                Arguments.of(double.class, Double.class)
        );
    }

    @ParameterizedTest
    @MethodSource("primitives")
    void primitiveAndWrapperGiveWrapperInstance(Class<?> primitive, Class<?> wrapper) {
        assertInstanceOf(wrapper, generator.generate(primitive));
        assertInstanceOf(wrapper, generator.generate(wrapper));
    }

    @ParameterizedTest
    @MethodSource("primitives")
    void valuesVary(Class<?> primitive, Class<?> wrapper) {
        Set<Object> values = Stream.generate(() -> generator.generate(primitive)).limit(50).collect(Collectors.toSet());
        assertTrue(values.size() > 1, wrapper + ": " + values);
    }

    @Test
    void valuesStayInRanges() {
        for (int i = 0; i < 1_000; i++) {
            int intValue = generator.generate(int.class);
            long longValue = generator.generate(long.class);
            double doubleValue = generator.generate(double.class);
            float floatValue = generator.generate(float.class);
            char charValue = generator.generate(char.class);

            assertTrue(intValue >= -1_000 && intValue <= 1_000);
            assertTrue(longValue >= -1_000_000 && longValue <= 1_000_000);
            assertTrue(doubleValue >= -1_000 && doubleValue < 1_000);
            assertTrue(floatValue >= -1_000 && floatValue < 1_000);
            assertTrue(charValue >= 'a' && charValue <= 'z');
        }
    }
}
