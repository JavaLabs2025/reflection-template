package org.example.testclasses;

import org.example.generator.Generatable;

@Generatable
public class AmbiguousConstructors {
    @Generatable
    public AmbiguousConstructors(int value) {
    }

    @Generatable
    public AmbiguousConstructors(String value) {
    }
}
