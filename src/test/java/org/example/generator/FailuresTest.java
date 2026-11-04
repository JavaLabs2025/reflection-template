package org.example.generator;

import org.example.testclasses.Exploding;
import org.example.testclasses.Outer;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FailuresTest {
    private final Generator generator = new Generator(new Random(42));

    @Test
    void nullTypeIsRejected() {
        assertThrows(NullPointerException.class, () -> generator.generateValueOfType((Type) null));
    }

    @Test
    void constructorExceptionIsPreservedAsCause() {
        GenerationException exception = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(Exploding.class));
        IllegalStateException cause = assertInstanceOf(IllegalStateException.class, exception.getCause());
        assertTrue(cause.getMessage().startsWith("boom"));
        assertTrue(exception.getMessage().contains(Exploding.class.getName()));
    }

    @Test
    void innerClassIsRejected() {
        GenerationException exception = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(Outer.Inner.class));
        assertTrue(exception.getMessage().contains("Inner"));
    }

    @Test
    void failureInNestedTypeIsPropagated() {
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(Exploding[].class));
    }
}
