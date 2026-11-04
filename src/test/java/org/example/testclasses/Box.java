package org.example.testclasses;

import org.example.classes.Shape;
import org.example.generator.Generatable;

@Generatable
public class Box<T extends Shape> {
    private final T content;

    public Box(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }
}
