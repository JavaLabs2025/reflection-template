package org.example;

import org.example.classes.BinaryTreeNode;
import org.example.classes.Cart;
import org.example.classes.Example;
import org.example.classes.Product;
import org.example.classes.Rectangle;
import org.example.classes.Shape;
import org.example.classes.Triangle;
import org.example.generator.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GenerateExample {
    public static void main(String[] args) {
        Generator generator = new Generator();
        List<Class<?>> types = List.of(
                int.class, Double.class, char.class, boolean.class, String.class,
                int[].class, String[].class, List.class, Map.class,
                Example.class, Rectangle.class, Triangle.class, Shape.class, Shape[].class,
                Product.class, Cart.class, BinaryTreeNode.class
        );
        for (Class<?> type : types) {
            System.out.printf("%-14s -> %s%n", type.getSimpleName(), describe(generator.generateValueOfType(type)));
        }
    }

    private static String describe(Object value) {
        return switch (value) {
            case null -> "_";
            case Product p -> String.format(Locale.ROOT, "Product(%s, %.2f)", p.getName(), p.getPrice());
            case Cart c -> "Cart" + c.getItems().stream().map(GenerateExample::describe).toList();
            case BinaryTreeNode n -> "(" + describe(n.getLeft()) + " " + n.getData() + " " + describe(n.getRight()) + ")";
            case Object o when o.getClass().isArray() -> {
                String text = Arrays.deepToString(new Object[]{o});
                yield text.substring(1, text.length() - 1);
            }
            default -> String.valueOf(value);
        };
    }
}
