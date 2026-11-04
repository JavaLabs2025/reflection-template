package org.example.testclasses;

public enum Operation {
    PLUS {
        @Override
        public int apply(int left, int right) {
            return left + right;
        }
    },
    MINUS {
        @Override
        public int apply(int left, int right) {
            return left - right;
        }
    };

    public abstract int apply(int left, int right);
}
