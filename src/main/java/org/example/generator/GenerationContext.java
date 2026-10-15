package org.example.generator;

import java.lang.reflect.Type;
import java.util.Random;

public final class GenerationContext {

    public static final int MAX_DEPTH = 5;
    public static final int MAX_SIZE = 5;

    private final Generator generator;
    private final Random random;
    private final int depth;

    GenerationContext(Generator generator, Random random, int depth) {
        this.generator = generator;
        this.random = random;
        this.depth = depth;
    }

    public Random random() {
        return random;
    }

    public int depth() {
        return depth;
    }

    public boolean isDepthLimitReached() {
        return depth > MAX_DEPTH;
    }

    public int nextSize() {
        return random.nextInt(1, MAX_SIZE + 1);
    }

    public Object generate(Type type) {
        return generator.generate(type, depth + 1);
    }
}
