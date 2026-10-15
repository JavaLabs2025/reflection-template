package org.example.generator.collection;

import org.example.generator.GenerationContext;
import org.example.generator.TypeGenerator;
import org.example.generator.Types;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class ArrayGenerator implements TypeGenerator {

    @Override
    public boolean supports(Class<?> type) {
        return type.isArray();
    }

    @Override
    public Object generate(Type type, GenerationContext context) {
        Type component = type instanceof GenericArrayType array
                ? array.getGenericComponentType()
                : Types.rawClass(type).getComponentType();

        int size = context.nextSize();
        Object result = Array.newInstance(Types.rawClass(component), size);
        for (int i = 0; i < size; i++) {
            Array.set(result, i, context.generate(component));
        }
        return result;
    }
}
