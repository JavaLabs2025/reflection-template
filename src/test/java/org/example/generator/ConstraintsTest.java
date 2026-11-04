package org.example.generator;

import org.example.classes.Product;
import org.example.classes.Rectangle;
import org.example.classes.Triangle;
import org.example.testclasses.BadRange;
import org.example.testclasses.Circle;
import org.example.testclasses.Dice;
import org.example.testclasses.Impossible;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConstraintsTest {
    private final Generator generator = new Generator(new Random(42));

    @Test
    void rectangleSidesRespectRange() {
        for (Rectangle rectangle : Samples.of(generator, Rectangle.class)) {
            assertTrue(rectangle.getLength() >= 0.1 && rectangle.getLength() <= 100);
            assertTrue(rectangle.getWidth() >= 0.1 && rectangle.getWidth() <= 100);
        }
    }

    @Test
    void triangleAlwaysSatisfiesTriangleInequality() {
        for (Triangle triangle : Samples.of(generator, Triangle.class)) {
            assertTrue(triangle.isValid());
            assertTrue(triangle.getArea() > 0);
            assertFalse(Double.isNaN(triangle.getArea()));
        }
    }

    @Test
    void productPriceRespectsRange() {
        Samples.of(generator, Product.class)
                .forEach(product -> assertTrue(product.getPrice() >= 0.01 && product.getPrice() <= 10000));
    }

    @Test
    void circleRadiusRespectsRange() {
        Samples.of(generator, Circle.class)
                .forEach(circle -> assertTrue(circle.getRadius() >= 0.1 && circle.getRadius() <= 50));
    }

    @Test
    void integerRangeIsInclusive() {
        Set<Integer> values = Samples.of(generator, Dice.class).stream().map(Dice::value).collect(Collectors.toSet());
        assertEquals(IntStream.rangeClosed(1, 6).boxed().collect(Collectors.toSet()), values);
    }

    @Test
    void unsatisfiableInvariantIsReported() {
        GenerationException exception = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(Impossible.class));
        assertTrue(exception.getMessage().contains("@Invariant"));
    }

    @Test
    void rangeOnNonNumericParameterIsRejected() {
        GenerationException exception = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(BadRange.class));
        assertTrue(exception.getMessage().contains("@Range"));
    }
}
