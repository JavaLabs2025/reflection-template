package org.example;

import org.example.classes.Cart;
import org.example.classes.Example;
import org.example.classes.Shape;
import org.example.generator.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GenerateExample {
    public static void main(String[] args) {
        var gen = new Generator();

        Example example = gen.generate();
        Shape shape = gen.generate(Shape.class);
        int[] numbers = gen.generate();
        List<?> items = gen.generate(List.class);
        Map<?, ?> entries = gen.generate(Map.class);
        Cart cart = gen.generate();

        System.out.println(example);
        System.out.println(shape.getClass().getSimpleName() + ", area = " + shape.getArea());
        System.out.println(Arrays.toString(numbers));
        System.out.println(items);
        System.out.println(entries);
        System.out.println("cart items: " + cart.getItems().size());
    }
}
