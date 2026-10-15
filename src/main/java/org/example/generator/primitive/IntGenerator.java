package org.example.generator.primitive;

import java.util.Random;

public class IntGenerator extends PrimitiveGenerator<Integer> {

    private static final int BOUND = 1_000;

    public IntGenerator() {
        super(int.class, Integer.class);
    }

    @Override
    protected Integer next(Random random) {
        return random.nextInt(-BOUND, BOUND + 1);
    }
}
