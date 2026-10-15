package org.example.generator.primitive;

import org.example.generator.GenerationContext;
import org.example.generator.TypeGenerator;

import java.lang.reflect.Type;
import java.util.Random;

public abstract class PrimitiveGenerator<T> implements TypeGenerator {

    private final Class<T> primitive;
    private final Class<T> wrapper;

    protected PrimitiveGenerator(Class<T> primitive, Class<T> wrapper) {
        this.primitive = primitive;
        this.wrapper = wrapper;
    }

    @Override
    public boolean supports(Class<?> type) {
        return type == primitive || type == wrapper;
    }

    @Override
    public T generate(Type type, GenerationContext context) {
        return next(context.random());
    }

    protected abstract T next(Random random);
}
