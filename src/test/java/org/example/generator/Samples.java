package org.example.generator;

import java.util.List;
import java.util.stream.IntStream;

final class Samples {
    static final int RUNS = 200;

    private Samples() {
    }

    static <T> List<T> of(Generator generator, Class<T> type) {
        return IntStream.range(0, RUNS).mapToObj(i -> generator.generateValueOfType(type)).toList();
    }
}
