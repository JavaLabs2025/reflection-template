package org.example.generator.primitive;

import java.util.Random;

public class ByteGenerator extends PrimitiveGenerator<Byte> {

    public ByteGenerator() {
        super(byte.class, Byte.class);
    }

    @Override
    protected Byte next(Random random) {
        return (byte) random.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1);
    }
}
