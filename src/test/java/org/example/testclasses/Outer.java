package org.example.testclasses;

import org.example.generator.Generatable;

public class Outer {
    @Generatable
    public class Inner {
    }

    @Generatable
    public static class Nested {
        private final int value;

        public Nested(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
