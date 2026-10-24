package org.example.generator;

import org.example.classes.BinaryTreeNode;
import org.example.classes.Cart;
import org.example.classes.Example;
import org.example.classes.Product;
import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorTest {
    private static final int RUNS = 200;

    private final Generator generator = new Generator();

    public enum Color { RED, GREEN, BLUE }

    public enum EmptyEnum {
    }

    @Generatable
    public static class Person {
        public final String name;
        public final int age;
        public final Color favoriteColor;
        public final List<String> nicknames;
        public final Map<String, Integer> scores;
        public final List<? extends Animal> pets;

        public Person(String name, int age, Color favoriteColor, List<String> nicknames,
                      Map<String, Integer> scores, List<? extends Animal> pets) {
            this.name = name;
            this.age = age;
            this.favoriteColor = favoriteColor;
            this.nicknames = nicknames;
            this.scores = scores;
            this.pets = pets;
        }
    }

    @Generatable
    public abstract static class Animal {
    }

    @Generatable
    public static class Dog extends Animal {
        public Dog(String name) {
        }
    }

    @Generatable
    public static class Cat extends Animal {
        public Cat(int lives) {
        }
    }

    public static class NotAnnotatedAnimal extends Animal {
        public NotAnnotatedAnimal() {
        }
    }

    public static class NotAnnotated {
        public NotAnnotated() {
        }
    }

    @Generatable
    public static class NotAnnotatedHolder {
        public NotAnnotatedHolder(NotAnnotated value) {
        }
    }

    @Generatable
    public interface NoImplementations {
    }

    @Generatable
    public static class NoPublicConstructor {
        private NoPublicConstructor() {
        }
    }

    @Generatable
    public static class FailingConstructor {
        static int calls;

        public FailingConstructor(int value) {
            calls++;
            throw new IllegalStateException("broken");
        }
    }

    @Generatable
    public static class AlwaysInvalid {
        static int calls;

        public AlwaysInvalid(int value) {
            calls++;
            throw new IllegalArgumentException("never valid");
        }
    }

    @Generatable
    public static class Ranged {
        public final int small;
        public final double ratio;

        public Ranged(@Range(min = 1, max = 3) int small, @Range(min = -5, max = 5) double ratio) {
            this.small = small;
            this.ratio = ratio;
        }
    }

    @Generatable
    public static class RangeOnString {
        public RangeOnString(@Range(min = 1, max = 3) String text) {
        }
    }

    @Generatable
    public static class EmptyRange {
        public EmptyRange(@Range(min = 5, max = 5) int value) {
        }
    }

    @Test
    void primitivesAndWrappers() {
        Map<Class<?>, Class<?>> wrappers = Map.of(
                int.class, Integer.class, long.class, Long.class, short.class, Short.class, byte.class, Byte.class,
                double.class, Double.class, float.class, Float.class, boolean.class, Boolean.class, char.class, Character.class);
        wrappers.forEach((primitive, wrapper) -> {
            assertInstanceOf(wrapper, generator.generateValueOfType(primitive));
            assertInstanceOf(wrapper, generator.generateValueOfType(wrapper));
        });
    }

    @Test
    void numbersAndStringsAreRandom() {
        Set<Integer> numbers = new HashSet<>();
        Set<String> strings = new HashSet<>();
        for (int i = 0; i < RUNS; i++) {
            int number = generator.generateValueOfType(int.class);
            assertTrue(number >= Generator.MIN_NUMBER && number <= Generator.MAX_NUMBER, "number " + number);
            numbers.add(number);

            String string = generator.generateValueOfType(String.class);
            assertTrue(string.matches("[a-zA-Z0-9]{1," + Generator.MAX_STRING_LENGTH + "}"), string);
            strings.add(string);
        }
        assertTrue(numbers.size() > 1);
        assertTrue(strings.size() > 1);
    }

    @Test
    void enums() {
        Set<Color> colors = new HashSet<>();
        for (int i = 0; i < RUNS; i++) {
            colors.add(generator.generateValueOfType(Color.class));
        }
        assertEquals(Set.of(Color.values()), colors);
    }

    @Test
    void arrays() {
        assertSizeInRange(generator.generateValueOfType(int[].class).length);
        assertFilled(generator.generateValueOfType(String[].class));
        assertFilled(generator.generateValueOfType(Color[].class));
        assertFilled(generator.generateValueOfType(Rectangle[].class));
    }

    @Test
    void listAndMap() {
        List<?> list = generator.generateValueOfType(List.class);
        assertSizeInRange(list.size());
        for (Object element : list) {
            assertInstanceOf(Integer.class, element);
        }

        Map<?, ?> map = generator.generateValueOfType(Map.class);
        assertSizeInRange(map.size());
        map.forEach((key, value) -> {
            assertInstanceOf(String.class, key);
            assertInstanceOf(Integer.class, value);
        });
    }

    @Test
    void templateClasses() {
        assertNotNull(generator.generateValueOfType(Example.class));
        assertNotNull(generator.generateValueOfType(Rectangle.class));
        assertNotNull(generator.generateValueOfType(Triangle.class));
        assertNotNull(generator.generateValueOfType(Product.class).getName());

        Cart cart = generator.generateValueOfType(Cart.class);
        assertSizeInRange(cart.getItems().size());
        for (Object item : cart.getItems()) {
            assertInstanceOf(Product.class, item);
        }

        BinaryTreeNode tree = generator.generateValueOfType(BinaryTreeNode.class);
        assertTrue(height(tree) <= Generator.MAX_DEPTH + 1);
    }

    @Test
    void ownClass() {
        Person person = generator.generateValueOfType(Person.class);
        assertNotNull(person.name);
        assertNotNull(person.favoriteColor);

        assertSizeInRange(person.nicknames.size());
        for (Object nickname : person.nicknames) {
            assertInstanceOf(String.class, nickname);
        }

        assertSizeInRange(person.scores.size());
        for (Map.Entry<?, ?> entry : person.scores.entrySet()) {
            assertInstanceOf(String.class, entry.getKey());
            assertInstanceOf(Integer.class, entry.getValue());
        }

        assertSizeInRange(person.pets.size());
        for (Object pet : person.pets) {
            assertInstanceOf(Animal.class, pet);
        }
    }

    @Test
    void shapeInterface() {
        Set<Class<?>> seen = new HashSet<>();
        for (int i = 0; i < RUNS; i++) {
            Shape shape = generator.generateValueOfType(Shape.class);
            seen.add(shape.getClass());
        }
        assertEquals(Set.of(Rectangle.class, Triangle.class), seen);
    }

    @Test
    void abstractClassUsesOnlyAnnotatedSubclasses() {
        Set<Class<?>> seen = new HashSet<>();
        for (int i = 0; i < RUNS; i++) {
            seen.add(generator.generateValueOfType(Animal.class).getClass());
        }
        assertEquals(Set.of(Dog.class, Cat.class), seen);
    }

    @Test
    void typesWithoutGeneratableAreRejected() {
        GenerationException direct = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(NotAnnotated.class));
        assertTrue(direct.getMessage().contains("@Generatable"), direct.getMessage());

        GenerationException nested = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(NotAnnotatedHolder.class));
        assertTrue(nested.getMessage().contains("'value'"), nested.getMessage());

        assertThrows(GenerationException.class, () -> generator.generateValueOfType(NotAnnotatedAnimal.class));
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(NoImplementations.class));
    }

    @Test
    void impossibleRequestsThrow() {
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(void.class));
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(Object.class));
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(Set.class));
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(null));
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(EmptyEnum.class));
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(NoPublicConstructor.class));
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(RangeOnString.class));
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(EmptyRange.class));
    }

    @Test
    void onlyIllegalArgumentIsRetried() {
        FailingConstructor.calls = 0;
        GenerationException failing = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(FailingConstructor.class));
        assertInstanceOf(IllegalStateException.class, failing.getCause());
        assertEquals(1, FailingConstructor.calls);

        AlwaysInvalid.calls = 0;
        GenerationException invalid = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(AlwaysInvalid.class));
        assertInstanceOf(IllegalArgumentException.class, invalid.getCause());
        assertEquals(Generator.MAX_ATTEMPTS, AlwaysInvalid.calls);
    }

    @Test
    void rangeIsRespected() {
        Set<Integer> smallValues = new HashSet<>();
        for (int i = 0; i < RUNS; i++) {
            Ranged ranged = generator.generateValueOfType(Ranged.class);
            assertTrue(ranged.ratio >= -5 && ranged.ratio < 5, "ratio " + ranged.ratio);
            smallValues.add(ranged.small);
        }
        assertEquals(Set.of(1, 2, 3), smallValues);
    }

    @Test
    void shapesSatisfyDomainConstraints() throws ReflectiveOperationException {
        assertThrows(IllegalArgumentException.class, () -> new Rectangle(-1, 2));
        assertThrows(IllegalArgumentException.class, () -> new Triangle(1, 2, 10));

        for (int i = 0; i < RUNS; i++) {
            Rectangle rectangle = generator.generateValueOfType(Rectangle.class);
            assertSideInRange(field(rectangle, "length"));
            assertSideInRange(field(rectangle, "width"));

            Triangle triangle = generator.generateValueOfType(Triangle.class);
            double a = field(triangle, "sideA");
            double b = field(triangle, "sideB");
            double c = field(triangle, "sideC");
            assertSideInRange(a);
            assertSideInRange(b);
            assertSideInRange(c);
            assertTrue(a + b > c && a + c > b && b + c > a, a + ", " + b + ", " + c);
        }
    }

    @Test
    void sameSeedGivesSameValues() {
        Generator first = new Generator(42);
        Generator second = new Generator(42);
        for (int i = 0; i < 20; i++) {
            assertEquals(first.generateValueOfType(Map.class), second.generateValueOfType(Map.class));
            assertEquals(first.generateValueOfType(Shape.class).getClass(), second.generateValueOfType(Shape.class).getClass());
        }
    }

    private static void assertSizeInRange(int size) {
        assertTrue(size >= Generator.MIN_SIZE && size <= Generator.MAX_SIZE, "size " + size);
    }

    private static void assertFilled(Object[] array) {
        assertSizeInRange(array.length);
        for (Object element : array) {
            assertNotNull(element);
        }
    }

    private static int height(BinaryTreeNode node) {
        return node == null ? 0 : 1 + Math.max(height(node.getLeft()), height(node.getRight()));
    }

    private static void assertSideInRange(double side) {
        assertTrue(side >= 1 && side < 100, "side " + side);
    }

    private static double field(Object object, String name) throws ReflectiveOperationException {
        Field field = object.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.getDouble(object);
    }
}
