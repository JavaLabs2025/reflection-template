package org.example.generator.collection;

import org.example.generator.Generator;
import org.example.generator.Types;
import org.example.generator.samples.Samples.Person;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class MapGeneratorTest {

    private final Generator generator = new Generator(new Random(42));

    @Test
    void rawMapIsFilledWithDefaultEntries() {
        Map<?, ?> entries = generator.generate(Map.class);

        assertFalse(entries.isEmpty());
        entries.forEach((key, value) -> {
            assertInstanceOf(Types.DEFAULT_ELEMENT_TYPE, key);
            assertInstanceOf(Types.DEFAULT_ELEMENT_TYPE, value);
        });
    }

    @Test
    void hashMapIsSupported() {
        HashMap<?, ?> entries = generator.generate(HashMap.class);
        assertFalse(entries.isEmpty());
    }

    @Test
    void keyAndValueTypesAreTakenFromConstructorSignature() {
        Person person = generator.generate(Person.class);

        assertFalse(person.attributes().isEmpty());
        person.attributes().forEach((key, value) -> {
            assertInstanceOf(String.class, key);
            assertInstanceOf(Double.class, value);
        });
    }
}
