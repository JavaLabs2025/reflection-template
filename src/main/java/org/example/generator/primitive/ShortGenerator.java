package org.example.generator.primitive;

import java.util.Random;

public class ShortGenerator extends PrimitiveGenerator<Short> {

    public ShortGenerator() {
        super(short.class, Short.class);
    }

    @Override
    protected Short next(Random random) {
        return (short) random.nextInt(Short.MIN_VALUE, Short.MAX_VALUE + 1);
    }
}
