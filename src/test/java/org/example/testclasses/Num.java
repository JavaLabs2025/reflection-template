package org.example.testclasses;

import org.example.generator.Generatable;

@Generatable
public record Num(int value) implements Expr {
    @Override
    public int height() {
        return 1;
    }
}
