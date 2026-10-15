package org.example.generator.simple;

import org.example.generator.GenerationContext;
import org.example.generator.GenerationException;
import org.example.generator.TypeGenerator;
import org.example.generator.Types;

import java.lang.reflect.Type;

public class EnumGenerator implements TypeGenerator {

    @Override
    public boolean supports(Class<?> type) {
        return type.isEnum();
    }

    @Override
    public Object generate(Type type, GenerationContext context) {
        Class<?> enumClass = Types.rawClass(type);
        Object[] constants = enumClass.getEnumConstants();
        if (constants.length == 0) {
            throw new GenerationException("Перечисление " + enumClass.getName() + " не содержит констант");
        }
        return constants[context.random().nextInt(constants.length)];
    }

}
