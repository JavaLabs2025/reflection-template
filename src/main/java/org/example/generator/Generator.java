package org.example.generator;

import java.util.Map;
import java.util.function.Function;
import java.util.random.RandomGenerator;

public class Generator {
    private final RandomGenerator random = RandomGenerator.getDefault();

    private static final Map<Class<?>, Function<RandomGenerator, Object>> GENERATORS = Map.of(
            boolean.class, RandomGenerator::nextBoolean,
            byte.class, r -> (byte) r.nextInt(),
            short.class, r -> (short) r.nextInt(),
            char.class, r -> (char) r.nextInt('a', 'z' + 1),
            int.class, RandomGenerator::nextInt,
            long.class, RandomGenerator::nextLong,
            float.class, RandomGenerator::nextFloat,
            double.class, RandomGenerator::nextDouble
    );

    public Object generateValueOfType(Class<?> clazz) throws Throwable {
        java.lang.reflect.Constructor<?> ctor = clazz.getDeclaredConstructors()[0];

        Class<?>[] parameterTypes = ctor.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];

        for (int j = 0; j < parameterTypes.length; j++) {
            if (parameterTypes[j].isPrimitive() && parameterTypes[j] != void.class) {
                args[j] = GENERATORS.get(parameterTypes[j]).apply(random);
                System.out.println(args[j]);
            }
        }

        return ctor.newInstance(new StringBuilder("e"));
    }


}