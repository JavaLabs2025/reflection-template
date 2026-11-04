package org.example.testclasses;

import org.example.classes.Shape;
import org.example.generator.Generatable;
import org.example.generator.Range;

@Generatable
public class Circle implements Shape {
    private final double radius;

    public Circle(@Range(min = 0.1, max = 50) double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }

    @Override
    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }
}
