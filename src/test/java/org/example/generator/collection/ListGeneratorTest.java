package org.example.generator.collection;

import org.example.classes.Cart;
import org.example.classes.Product;
import org.example.generator.GenerationContext;
import org.example.generator.Generator;
import org.example.generator.Types;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ListGeneratorTest {

    private final Generator generator = new Generator(new Random(42));

    @Test
    void rawListIsFilledWithDefaultElements() {
        List<?> items = generator.generate(List.class);

        assertTrue(items.size() >= 1 && items.size() <= GenerationContext.MAX_SIZE);
        items.forEach(item -> assertInstanceOf(Types.DEFAULT_ELEMENT_TYPE, item));
    }

    @Test
    void arrayListIsSupported() {
        ArrayList<?> items = generator.generate(ArrayList.class);
        assertFalse(items.isEmpty());
    }

    @Test
    void elementTypeIsTakenFromConstructorSignature() {
        Cart cart = generator.generate(Cart.class);

        assertFalse(cart.getItems().isEmpty());
        cart.getItems().forEach(item -> assertInstanceOf(Product.class, item));
    }
}
