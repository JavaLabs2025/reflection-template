package org.example.generator.primitive;

import java.util.Random;

public class BooleanGenerator extends PrimitiveGenerator<Boolean> {

    public BooleanGenerator() {
        super(boolean.class, Boolean.class);
    }

    @Override
    protected Boolean next(Random random) {
        return random.nextBoolean();
    }
}
