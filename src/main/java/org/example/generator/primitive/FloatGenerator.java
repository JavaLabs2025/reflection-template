package org.example.generator.primitive;

import java.util.Random;

public class FloatGenerator extends PrimitiveGenerator<Float> {

    private static final float BOUND = 1_000;

    public FloatGenerator() {
        super(float.class, Float.class);
    }

    @Override
    protected Float next(Random random) {
        return random.nextFloat(-BOUND, BOUND);
    }
}
