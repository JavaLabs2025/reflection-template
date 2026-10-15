package org.example.generator;

import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneratorTest {

    private final Generator generator = new Generator(new Random(42));

    @Test
    void generatesByClass() {
        Integer number = generator.generate(Integer.class);
        Rectangle rectangle = generator.generate(Rectangle.class);

        assertNotNull(number);
        assertNotNull(rectangle);
    }

    @Test
    void infersTypeFromAssignment() {
        Integer number = generator.generate();
        String text = generator.generate();
        Shape shape = generator.generate();
        int[] numbers = generator.generate();
        int primitive = generator.generate();

        assertNotNull(number);
        assertNotNull(text);
        assertInstanceOf(Shape.class, shape);
        assertTrue(numbers.length > 0);
        assertTrue(Math.abs(primitive) <= 1_000);
    }

    @Test
    void failsWhenTypeCannotBeInferred() {
        GenerationException e = assertThrows(GenerationException.class, () -> {
            Object value = generator.generate();
        });
        assertTrue(e.getMessage().contains("вывести тип"), e.getMessage());
    }

    @Test
    void rejectsArgumentsInInferringOverload() {
        assertThrows(IllegalArgumentException.class, () -> generator.generate("a", "b"));
    }

    @Test
    void valuesAreNotConstant() {
        Set<Object> values = Stream.generate(() -> generator.generate(String.class)).limit(20).collect(Collectors.toSet());
        assertTrue(values.size() > 1);
    }

    @Test
    void sameSeedGivesSameValues() {
        Generator first = new Generator(new Random(7));
        Generator second = new Generator(new Random(7));
        assertEquals(first.generate(String.class), second.generate(String.class));
    }

    @Test
    void voidIsNotSupported() {
        GenerationException e = assertThrows(GenerationException.class, () -> generator.generate(void.class));
        assertTrue(e.getMessage().contains("не поддерживается"), e.getMessage());
    }

    @Test
    void customGeneratorTakesPrecedence() {
        TypeGenerator constant = new TypeGenerator() {
            @Override
            public boolean supports(Class<?> type) {
                return type == String.class;
            }

            @Override
            public Object generate(Type type, GenerationContext context) {
                return "custom";
            }
        };
        List<TypeGenerator> generators = new ArrayList<>(Generator.defaultGenerators());
        generators.addFirst(constant);
        Generator custom = new Generator(new Random(), generators);

        assertEquals("custom", custom.generate(String.class));
        List<?> list = custom.generate(List.class);
        assertTrue(list.stream().allMatch("custom"::equals));
    }
}
