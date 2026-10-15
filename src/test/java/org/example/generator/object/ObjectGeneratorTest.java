package org.example.generator.object;

import org.example.classes.BinaryTreeNode;
import org.example.classes.Example;
import org.example.classes.Product;
import org.example.classes.Rectangle;
import org.example.classes.Triangle;
import org.example.generator.GenerationContext;
import org.example.generator.GenerationException;
import org.example.generator.Generator;
import org.example.generator.samples.Samples.Account;
import org.example.generator.samples.Samples.Animal;
import org.example.generator.samples.Samples.Faulty;
import org.example.generator.samples.Samples.HasNotGeneratable;
import org.example.generator.samples.Samples.NoPublicConstructor;
import org.example.generator.samples.Samples.NotGeneratable;
import org.example.generator.samples.Samples.Person;
import org.example.generator.samples.Samples.Vehicle;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectGeneratorTest {

    private final Generator generator = new Generator(new Random(42));

    @Test
    void generatesTemplateClasses() {
        assertNotNull(generator.generate(Example.class));
        assertNotNull(generator.generate(Product.class).getName());
        assertTrue(Double.isFinite(generator.generate(Rectangle.class).getPerimeter()));
        assertTrue(Double.isFinite(generator.generate(Triangle.class).getPerimeter()));
    }

    @Test
    void generatesNewClassWithAllSupportedCategories() {
        Person person = generator.generate(Person.class);

        assertNotNull(person.name());
        assertTrue(person.age() >= -1_000 && person.age() <= 1_000);
        assertNotNull(person.color());
        assertInstanceOf(Animal.class, person.pet());
        assertInstanceOf(Vehicle.class, person.vehicle());
        assertFalse(person.scores().isEmpty());
        person.scores().forEach(score -> assertInstanceOf(Integer.class, score));
        assertFalse(person.attributes().isEmpty());
        assertTrue(person.favourites().length > 0);
    }

    @Test
    void recursiveStructureIsCutAtMaxDepth() {
        BinaryTreeNode root = generator.generate(BinaryTreeNode.class);

        assertNotNull(root.getData());
        assertNotNull(root.getLeft());
        assertEquals(GenerationContext.MAX_DEPTH + 1, height(root));
    }

    @Test
    void annotatedConstructorIsPreferred() {
        for (int i = 0; i < 20; i++) {
            assertEquals("annotated", generator.generate(Account.class).createdBy());
        }
    }

    @Test
    void failsOnClassWithoutAnnotation() {
        GenerationException e = assertThrows(GenerationException.class, () -> generator.generate(NotGeneratable.class));
        assertTrue(e.getMessage().contains("не помечен @Generatable"), e.getMessage());
    }

    @Test
    void failsOnNestedClassWithoutAnnotation() {
        GenerationException e = assertThrows(GenerationException.class, () -> generator.generate(HasNotGeneratable.class));
        assertTrue(e.getMessage().contains(NotGeneratable.class.getName()), e.getMessage());
    }

    @Test
    void failsWithoutUsableConstructor() {
        GenerationException e = assertThrows(GenerationException.class, () -> generator.generate(NoPublicConstructor.class));
        assertTrue(e.getMessage().contains("нет ни публичных конструкторов"), e.getMessage());
    }

    @Test
    void wrapsConstructorException() {
        GenerationException e = assertThrows(GenerationException.class, () -> generator.generate(Faulty.class));
        assertTrue(e.getMessage().contains("выбросил исключение"), e.getMessage());
        assertInstanceOf(IllegalStateException.class, e.getCause());
    }

    private static int height(BinaryTreeNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(height(node.getLeft()), height(node.getRight()));
    }
}
