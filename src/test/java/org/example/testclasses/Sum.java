package org.example.testclasses;

import org.example.generator.Generatable;

@Generatable
public record Sum(Expr left, Expr right) implements Expr {
    @Override
    public int height() {
        return 1 + Math.max(Expr.heightOf(left), Expr.heightOf(right));
    }
}
