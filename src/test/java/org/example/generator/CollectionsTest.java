package org.example.generator;

import org.example.classes.Shape;
import org.example.testclasses.Color;
import org.example.testclasses.Point;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionsTest {
    private final Generator generator = new Generator(new Random(42));

    @Test
    void rawListContainsDefaultElements() {
        for (List<?> list : Samples.of(generator, List.class)) {
            assertTrue(list.size() >= 1 && list.size() <= Generator.MAX_SIZE);
            list.forEach(element -> assertInstanceOf(Integer.class, element));
        }
    }

    @Test
    void rawMapContainsDefaultEntries() {
        for (Map<?, ?> map : Samples.of(generator, Map.class)) {
            assertFalse(map.isEmpty());
            map.forEach((key, value) -> {
                assertInstanceOf(String.class, key);
                assertInstanceOf(Integer.class, value);
            });
        }
    }

    @ParameterizedTest
    @ValueSource(classes = {Collection.class, Iterable.class, ArrayList.class})
    void listCompatibleTypes(Class<?> type) {
        assertInstanceOf(ArrayList.class, generator.generateValueOfType(type));
    }

    @Test
    void hashMapIsSupported() {
        assertInstanceOf(HashMap.class, generator.generateValueOfType(HashMap.class));
    }

    @Test
    void parameterizedListContainsRequestedElements() {
        List<?> list = assertInstanceOf(List.class, generator.generateValueOfType(new TypeRef<List<Point>>() {}.type));
        assertFalse(list.isEmpty());
        list.forEach(element -> assertInstanceOf(Point.class, element));
    }

    @Test
    void nestedParameterizedMap() {
        Map<?, ?> map = assertInstanceOf(Map.class,
                generator.generateValueOfType(new TypeRef<Map<Color, List<Integer>>>() {}.type));
        assertFalse(map.isEmpty());
        map.forEach((key, value) -> {
            assertInstanceOf(Color.class, key);
            List<?> elements = assertInstanceOf(List.class, value);
            elements.forEach(element -> assertInstanceOf(Integer.class, element));
        });
    }

    @Test
    void mapWithSmallKeyDomainDoesNotHang() {
        for (int i = 0; i < Samples.RUNS; i++) {
            Map<?, ?> map = assertInstanceOf(Map.class,
                    generator.generateValueOfType(new TypeRef<Map<Boolean, String>>() {}.type));
            assertTrue(map.size() >= 1 && map.size() <= 2);
        }
    }

    @Test
    void wildcardUsesBound() {
        List<?> list = assertInstanceOf(List.class,
                generator.generateValueOfType(new TypeRef<List<? extends Shape>>() {}.type));
        list.forEach(element -> assertInstanceOf(Shape.class, element));
    }

    @Test
    void unsupportedCollectionIsRejected() {
        assertThrows(GenerationException.class, () -> generator.generateValueOfType(Set.class));
    }

    @Test
    void unsupportedParameterizedTypeIsRejected() {
        GenerationException exception = assertThrows(GenerationException.class,
                () -> generator.generateValueOfType(new TypeRef<Optional<String>>() {}.type));
        assertTrue(exception.getMessage().contains("not supported"));
    }
}
