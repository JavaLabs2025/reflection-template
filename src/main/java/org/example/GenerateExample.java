package org.example;


import org.example.classes.Cart;
import org.example.classes.Example;
import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.example.generator.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GenerateExample {
    public static void main(String[] args) {
        var gen = new Generator();

        Integer number = gen.generateValueOfType(Integer.class);
        String text = gen.generateValueOfType(String.class);
        Example example = gen.generateValueOfType(Example.class);
        Rectangle rectangle = gen.generateValueOfType(Rectangle.class);
        Triangle triangle = gen.generateValueOfType(Triangle.class);
        Shape shape = gen.generateValueOfType(Shape.class);
        int[] numbers = gen.generateValueOfType(int[].class);
        List<?> items = gen.generateValueOfType(List.class);
        Map<?, ?> entries = gen.generateValueOfType(Map.class);
        Cart cart = gen.generateValueOfType(Cart.class);

        System.out.println("Integer:   " + number);
        System.out.println("String:    " + text);
        System.out.println("Example:   " + example);
        System.out.println("Rectangle: area = " + rectangle.getArea());
        System.out.println("Triangle:  area = " + triangle.getArea());
        System.out.println("Shape:     " + shape.getClass().getSimpleName() + ", area = " + shape.getArea());
        System.out.println("int[]:     " + Arrays.toString(numbers));
        System.out.println("List:      " + items);
        System.out.println("Map:       " + entries);
        System.out.println("Cart:      " + cart.getItems().size() + " products");
    }
}
