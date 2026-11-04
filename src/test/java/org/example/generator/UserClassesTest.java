package org.example.generator;

import org.example.classes.Shape;
import org.example.testclasses.Box;
import org.example.testclasses.Color;
import org.example.testclasses.Dog;
import org.example.testclasses.Drawing;
import org.example.testclasses.Expr;
import org.example.testclasses.Library;
import org.example.testclasses.Outer;
import org.example.testclasses.Point;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserClassesTest {
    private final Generator generator = new Generator(new Random(42));

    @Test
    void record() {
        assertTrue(new HashSet<>(Samples.of(generator, Point.class)).size() > 1);
    }

    @Test
    void staticNestedClass() {
        assertInstanceOf(Outer.Nested.class, generator.generateValueOfType(Outer.Nested.class));
    }

    @Test
    void compositeClass() {
        Library library = generator.generateValueOfType(Library.class);
        assertFalse(library.getTitle().isEmpty());
        assertFalse(library.getShelves().isEmpty());
        library.getShelves().forEach((name, points) -> {
            assertInstanceOf(String.class, name);
            points.forEach(point -> assertInstanceOf(Point.class, point));
        });
        assertTrue(library.getColors().length > 0);
        Arrays.stream(library.getColors()).forEach(color -> assertInstanceOf(Color.class, color));
        assertInstanceOf(Dog.class, library.getKeeper());
    }

    @Test
    void wildcardsAndArraysInsideMaps() {
        Drawing drawing = generator.generateValueOfType(Drawing.class);
        assertFalse(drawing.getShapes().isEmpty());
        drawing.getShapes().forEach(shape -> assertInstanceOf(Shape.class, shape));
        drawing.getLayers().forEach((color, shapes) -> {
            assertInstanceOf(Color.class, color);
            Arrays.stream(shapes).forEach(shape -> assertInstanceOf(Shape.class, shape));
        });
    }

    @Test
    void typeVariableUsesItsBound() {
        Box<?> box = generator.generateValueOfType(Box.class);
        assertInstanceOf(Shape.class, box.getContent());
    }

    @Test
    void recursionThroughInterfaceIsBounded() {
        for (Expr expr : Samples.of(generator, Expr.class)) {
            assertNotNull(expr);
            assertTrue(expr.height() <= Generator.MAX_DEPTH);
        }
    }
}
