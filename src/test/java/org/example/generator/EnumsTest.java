package org.example.generator;

import org.example.testclasses.Color;
import org.example.testclasses.EmptyEnum;
import org.example.testclasses.Operation;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnumsTest {
    private final Generator generator = new Generator(new Random(42));

    @Test
    void generatesEveryConstantOfEnum() {
        assertEquals(EnumSet.allOf(Color.class), new HashSet<>(Samples.of(generator, Color.class)));
    }

    @Test
    void enumWithConstantBodiesIsNotTreatedAsAbstractClass() {
        assertEquals(EnumSet.allOf(Operation.class), new HashSet<>(Samples.of(generator, Operation.class)));
    }

    @Test
    void emptyEnumIsRejected() {
        GenerationException exception = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(EmptyEnum.class));
        assertTrue(exception.getMessage().contains("no constants"));
    }
}
