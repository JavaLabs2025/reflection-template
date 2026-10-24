package org.example.classes;

import org.example.generator.Generatable;
import org.example.generator.Range;

@Generatable
public class Rectangle implements Shape {
    private double length;
    private double width;

    public Rectangle(@Range(min = 1, max = 100) double length,
                     @Range(min = 1, max = 100) double width) {
        if (!(length > 0 && width > 0)) {
            throw new IllegalArgumentException("Rectangle sides must be positive: " + length + ", " + width);
        }
        this.length = length;
        this.width = width;
    }

    @Override
    public double getArea() {
        return length * width;
    }

    @Override
    public double getPerimeter() {
        return 2 * (length + width);
    }
}