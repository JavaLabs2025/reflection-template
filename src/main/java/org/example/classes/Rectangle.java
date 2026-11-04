package org.example.classes;

import org.example.generator.Generatable;
import org.example.generator.Range;

import java.util.Locale;

@Generatable
public class Rectangle implements Shape {
    private double length;
    private double width;

    public Rectangle(@Range(min = 0.1, max = 100) double length,
                     @Range(min = 0.1, max = 100) double width) {
        this.length = length;
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    @Override
    public double getArea() {
        return length * width;
    }

    @Override
    public double getPerimeter() {
        return 2 * (length + width);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "Rectangle(%.2f x %.2f)", length, width);
    }
}
