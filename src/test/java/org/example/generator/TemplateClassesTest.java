package org.example.generator;

import org.example.classes.BinaryTreeNode;
import org.example.classes.Cart;
import org.example.classes.Example;
import org.example.classes.Product;
import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TemplateClassesTest {
    private final Generator generator = new Generator(new Random(42));

    @Test
    void example() {
        Example example = generator.generateValueOfType(Example.class);
        assertTrue(example.toString().matches("Example\\(-?\\d+ -?[\\d.E-]+\\)"), example.toString());
    }

    @Test
    void rectangle() {
        Samples.of(generator, Rectangle.class).forEach(r -> assertTrue(r.getArea() > 0 && r.getPerimeter() > 0));
    }

    @Test
    void triangle() {
        Samples.of(generator, Triangle.class).forEach(t -> assertTrue(t.isValid() && t.getArea() > 0));
    }

    @Test
    void product() {
        Product product = generator.generateValueOfType(Product.class);
        assertFalse(product.getName().isEmpty());
        assertTrue(product.getPrice() >= 0.01 && product.getPrice() <= 10000);
    }

    @Test
    void cartContainsGeneratedProducts() {
        for (Cart cart : Samples.of(generator, Cart.class)) {
            assertFalse(cart.getItems().isEmpty());
            cart.getItems().forEach(item -> {
                assertInstanceOf(Product.class, item);
                assertFalse(item.getName().isEmpty());
            });
        }
    }

    @Test
    void binaryTreeIsFiniteAndFilled() {
        BinaryTreeNode root = generator.generateValueOfType(BinaryTreeNode.class);
        int height = height(root);
        assertTrue(height >= 1 && height <= Generator.MAX_DEPTH);
        assertAllDataPresent(root);
    }

    @Test
    void shapeIsGeneratedFromImplementations() {
        Set<Class<?>> kinds = Samples.of(generator, Shape.class).stream()
                .map(Object::getClass)
                .collect(Collectors.toSet());
        assertTrue(kinds.containsAll(Set.of(Rectangle.class, Triangle.class)));
    }

    @Test
    void shapeImplementationsAreSearchedInConfiguredPackages() {
        Generator templateOnly = new Generator(new Random(42), "org.example.classes");
        Set<Class<?>> kinds = new HashSet<>();
        Samples.of(templateOnly, Shape.class).forEach(shape -> kinds.add(shape.getClass()));
        assertEquals(Set.of(Rectangle.class, Triangle.class), kinds);
    }

    private static int height(BinaryTreeNode node) {
        return node == null ? 0 : 1 + Math.max(height(node.getLeft()), height(node.getRight()));
    }

    private static void assertAllDataPresent(BinaryTreeNode node) {
        if (node != null) {
            assertNotNull(node.getData());
            assertAllDataPresent(node.getLeft());
            assertAllDataPresent(node.getRight());
        }
    }
}
