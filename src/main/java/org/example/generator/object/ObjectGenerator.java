package org.example.generator.object;

import org.example.generator.Generatable;
import org.example.generator.GenerationContext;
import org.example.generator.GenerationException;
import org.example.generator.TypeGenerator;
import org.example.generator.Types;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ObjectGenerator implements TypeGenerator {

    @Override
    public boolean supports(Class<?> type) {
        return !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
    }

    @Override
    public Object generate(Type type, GenerationContext context) {
        Class<?> clazz = Types.rawClass(type);
        if (!clazz.isAnnotationPresent(Generatable.class)) {
            throw new GenerationException("Класс " + clazz.getName() + " не помечен @" + Generatable.class.getSimpleName());
        }
        if (context.isDepthLimitReached()) {
            return null;
        }

        Constructor<?> constructor = chooseConstructor(clazz, context.random());
        Object[] args = Arrays.stream(parameterTypes(constructor)).map(context::generate).toArray();
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InvocationTargetException e) {
            throw new GenerationException("Конструктор " + constructor + " выбросил исключение", e.getCause());
        } catch (ReflectiveOperationException | RuntimeException e) {
            throw new GenerationException("Не удалось вызвать конструктор " + constructor, e);
        }
    }

    private static Constructor<?> chooseConstructor(Class<?> clazz, Random random) {
        List<Constructor<?>> annotated = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.isAnnotationPresent(Generatable.class))
                .toList();
        List<Constructor<?>> candidates = annotated.isEmpty() ? List.of(clazz.getConstructors()) : annotated;
        if (candidates.isEmpty()) {
            throw new GenerationException("У класса " + clazz.getName()
                    + " нет ни публичных конструкторов, ни конструкторов с @" + Generatable.class.getSimpleName());
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private static Type[] parameterTypes(Constructor<?> constructor) {
        Type[] generic = constructor.getGenericParameterTypes();
        return generic.length == constructor.getParameterCount() ? generic : constructor.getParameterTypes();
    }
}
