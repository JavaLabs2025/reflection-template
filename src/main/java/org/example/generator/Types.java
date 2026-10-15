package org.example.generator;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

public final class Types {

    public static final Class<?> DEFAULT_ELEMENT_TYPE = String.class;

    private Types() {
    }

    public static Class<?> rawClass(Type type) {
        return switch (type) {
            case Class<?> clazz -> clazz;
            case ParameterizedType parameterized -> (Class<?>) parameterized.getRawType();
            case GenericArrayType array -> rawClass(array.getGenericComponentType()).arrayType();
            case WildcardType wildcard -> rawClass(wildcard.getUpperBounds()[0]);
            case TypeVariable<?> variable -> rawClass(variable.getBounds()[0]);
            default -> throw new GenerationException("Неизвестный вид типа: " + type);
        };
    }

    public static Type typeArgument(Type type, int index) {
        if (type instanceof ParameterizedType parameterized) {
            Type argument = parameterized.getActualTypeArguments()[index];
            if (rawClass(argument) != Object.class) {
                return argument;
            }
        }
        return DEFAULT_ELEMENT_TYPE;
    }

}
