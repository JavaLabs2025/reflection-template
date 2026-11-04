package org.example.generator;

import org.example.classes.Shape;
import org.example.testclasses.AmbiguousConstructors;
import org.example.testclasses.Animal;
import org.example.testclasses.Cat;
import org.example.testclasses.Circle;
import org.example.testclasses.Dog;
import org.example.testclasses.NotAnnotated;
import org.example.testclasses.Orphan;
import org.example.testclasses.Secret;
import org.example.testclasses.TwoConstructors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratableSemanticsTest {
    private final Generator generator = new Generator(new Random(42));

    @ParameterizedTest
    @ValueSource(classes = {NotAnnotated.class, Cat.class, Object.class, StringBuilder.class})
    void classWithoutAnnotationIsRejected(Class<?> type) {
        GenerationException exception = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(type));
        assertTrue(exception.getMessage().contains("@Generatable"));
    }

    @Test
    void onlyAnnotatedImplementationsAreChosen() {
        Samples.of(generator, Animal.class).forEach(animal -> assertInstanceOf(Dog.class, animal));
    }

    @Test
    void interfaceWithoutAnnotatedImplementationsIsRejected() {
        GenerationException exception = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(Orphan.class));
        assertTrue(exception.getMessage().contains("No @Generatable implementations"));
    }

    @Test
    void annotatedConstructorIsUsed() {
        Samples.of(generator, TwoConstructors.class).forEach(value -> assertEquals("annotated", value.getSource()));
    }

    @Test
    void severalAnnotatedConstructorsAreRejected() {
        GenerationException exception = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(AmbiguousConstructors.class));
        assertTrue(exception.getMessage().contains("more than one @Generatable constructor"));
    }

    @Test
    void newImplementationIsDiscoveredWithoutChangingGenerator() {
        assertTrue(Samples.of(generator, Shape.class).stream().anyMatch(Circle.class::isInstance));
    }

    @Test
    void privateConstructorOfAnnotatedClassIsUsed() {
        assertInstanceOf(Secret.class, generator.generateValueOfType(Secret.class));
    }
}
