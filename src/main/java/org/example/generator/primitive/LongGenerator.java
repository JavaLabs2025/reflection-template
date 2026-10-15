package org.example.generator.primitive;

import java.util.Random;

public class LongGenerator extends PrimitiveGenerator<Long> {

    private static final long BOUND = 1_000_000;

    public LongGenerator() {
        super(long.class, Long.class);
    }

    @Override
    protected Long next(Random random) {
        return random.nextLong(-BOUND, BOUND + 1);
    }
}
