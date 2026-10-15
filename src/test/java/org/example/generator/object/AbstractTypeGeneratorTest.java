package org.example.generator.object;

import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.example.generator.GenerationException;
import org.example.generator.Generator;
import org.example.generator.samples.Samples.Animal;
import org.example.generator.samples.Samples.Bicycle;
import org.example.generator.samples.Samples.Cat;
import org.example.generator.samples.Samples.Dog;
import org.example.generator.samples.Samples.Plant;
import org.example.generator.samples.Samples.Truck;
import org.example.generator.samples.Samples.Vehicle;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractTypeGeneratorTest {

    private final Generator generator = new Generator(new Random(42));

    @Test
    void shapeIsOneOfItsImplementations() {
        assertEquals(Set.of(Rectangle.class, Triangle.class), generatedClasses(Shape.class));
    }

    @Test
    void implementationsWithoutAnnotationAreSkipped() {
        // Fish реализует Animal, но не помечен @Generatable
        assertEquals(Set.of(Cat.class, Dog.class), generatedClasses(Animal.class));
    }

    @Test
    void abstractClassIsSupported() {
        assertEquals(Set.of(Bicycle.class, Truck.class), generatedClasses(Vehicle.class));
    }

    @Test
    void failsWhenNoGeneratableImplementation() {
        GenerationException e = assertThrows(GenerationException.class, () -> generator.generate(Plant.class));
        assertTrue(e.getMessage().contains("Не найдено реализаций"), e.getMessage());
    }

    @Test
    void failsOnJdkInterfaceWithoutImplementations() {
        assertThrows(GenerationException.class, () -> generator.generate(Runnable.class));
    }

    private Set<Class<?>> generatedClasses(Class<?> type) {
        return Stream.generate(() -> generator.generate(type))
                .limit(100)
                .peek(value -> assertTrue(type.isInstance(value)))
                .map(Object::getClass)
                .collect(Collectors.toSet());
    }
}
