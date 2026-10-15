package org.example.generator.primitive;

import java.util.Random;

public class DoubleGenerator extends PrimitiveGenerator<Double> {

    private static final double BOUND = 1_000;

    public DoubleGenerator() {
        super(double.class, Double.class);
    }

    @Override
    protected Double next(Random random) {
        return random.nextDouble(-BOUND, BOUND);
    }
}
