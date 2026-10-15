package org.example.generator.simple;

import org.example.generator.GenerationException;
import org.example.generator.Generator;
import org.example.generator.samples.Samples.Color;
import org.example.generator.samples.Samples.NoConstants;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumGeneratorTest {

    private final Generator generator = new Generator(new Random(42));

    @Test
    void generatesEveryConstant() {
        Set<Color> colors = Stream.generate(() -> generator.generate(Color.class)).limit(100).collect(Collectors.toSet());
        assertEquals(EnumSet.allOf(Color.class), colors);
    }

    @Test
    void generatesEnumArray() {
        Color[] colors = generator.generate(Color[].class);
        assertTrue(colors.length > 0);
        assertTrue(Stream.of(colors).allMatch(c -> c != null));
    }

    @Test
    void failsOnEnumWithoutConstants() {
        GenerationException e = assertThrows(GenerationException.class, () -> generator.generate(NoConstants.class));
        assertTrue(e.getMessage().contains("не содержит констант"), e.getMessage());
    }
}
