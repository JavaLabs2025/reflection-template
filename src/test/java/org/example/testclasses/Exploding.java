package org.example.testclasses;

import org.example.generator.Generatable;

@Generatable
public class Exploding {
    public Exploding(int value) {
        throw new IllegalStateException("boom " + value);
    }
}
