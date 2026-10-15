package org.example.generator.collection;

import org.example.generator.GenerationContext;
import org.example.generator.TypeGenerator;
import org.example.generator.Types;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MapGenerator implements TypeGenerator {

    @Override
    public boolean supports(Class<?> type) {
        return Map.class.isAssignableFrom(type) && type.isAssignableFrom(HashMap.class);
    }

    @Override
    public Map<Object, Object> generate(Type type, GenerationContext context) {
        Type keyType = Types.typeArgument(type, 0);
        Type valueType = Types.typeArgument(type, 1);
        int size = context.nextSize();
        Map<Object, Object> result = new HashMap<>();
        for (int i = 0; i < size; i++) {
            result.put(context.generate(keyType), context.generate(valueType));
        }
        return result;
    }
}
