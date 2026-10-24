package org.example.classes;

import org.example.generator.Generatable;
import org.example.generator.Range;

@Generatable
public class Triangle implements Shape {
    private double sideA;
    private double sideB;
    private double sideC;

    public Triangle(@Range(min = 1, max = 100) double sideA,
                    @Range(min = 1, max = 100) double sideB,
                    @Range(min = 1, max = 100) double sideC) {
        if (!(sideA + sideB > sideC && sideA + sideC > sideB && sideB + sideC > sideA)) {
            throw new IllegalArgumentException("Sides do not form a triangle: " + sideA + ", " + sideB + ", " + sideC);
        }
        this.sideA = sideA;
        this.sideB = sideB;
        this.sideC = sideC;
    }

    @Override
    public double getArea() {
        double s = (sideA + sideB + sideC) / 2;
        return Math.sqrt(s * (s - sideA) * (s - sideB) * (s - sideC));
    }

    @Override
    public double getPerimeter() {
        return sideA + sideB + sideC;
    }
}