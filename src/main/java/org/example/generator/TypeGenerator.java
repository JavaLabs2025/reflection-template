package org.example.generator;

import java.lang.reflect.Type;

public interface TypeGenerator {

    boolean supports(Class<?> type);

    Object generate(Type type, GenerationContext context);

}
