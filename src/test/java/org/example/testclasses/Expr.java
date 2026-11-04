package org.example.testclasses;

public interface Expr {
    int height();

    static int heightOf(Expr expr) {
        return expr == null ? 0 : expr.height();
    }
}
