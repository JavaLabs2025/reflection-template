package org.example.generator.collection;

import org.example.generator.GenerationContext;
import org.example.generator.TypeGenerator;
import org.example.generator.Types;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListGenerator implements TypeGenerator {

    @Override
    public boolean supports(Class<?> type) {
        return List.class.isAssignableFrom(type) && type.isAssignableFrom(ArrayList.class);
    }

    @Override
    public List<Object> generate(Type type, GenerationContext context) {
        Type elementType = Types.typeArgument(type, 0);
        int size = context.nextSize();
        List<Object> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(context.generate(elementType));
        }
        return result;
    }
}
