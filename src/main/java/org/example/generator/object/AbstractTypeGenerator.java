package org.example.generator.object;

import org.example.generator.Generatable;
import org.example.generator.GenerationContext;
import org.example.generator.GenerationException;
import org.example.generator.TypeGenerator;
import org.example.generator.Types;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AbstractTypeGenerator implements TypeGenerator {

    private final Map<Class<?>, List<Class<?>>> implementations = new ConcurrentHashMap<>();

    @Override
    public boolean supports(Class<?> type) {
        return !type.isPrimitive() && !type.isArray()
                && (type.isInterface() || Modifier.isAbstract(type.getModifiers()));
    }

    @Override
    public Object generate(Type type, GenerationContext context) {
        Class<?> abstractType = Types.rawClass(type);
        List<Class<?>> candidates = implementations.computeIfAbsent(abstractType, AbstractTypeGenerator::findGeneratable);
        if (candidates.isEmpty()) {
            throw new GenerationException("Не найдено реализаций " + abstractType.getName()
                    + " с @" + Generatable.class.getSimpleName() + " в пакете '" + abstractType.getPackageName() + "'");
        }
        return context.generate(candidates.get(context.random().nextInt(candidates.size())));
    }

    private static List<Class<?>> findGeneratable(Class<?> abstractType) {
        return ImplementationScanner.findImplementations(abstractType).stream()
                .filter(c -> c.isAnnotationPresent(Generatable.class))
                .toList();
    }
}
