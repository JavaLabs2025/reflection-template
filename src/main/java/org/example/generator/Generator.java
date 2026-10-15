package org.example.generator;

import org.example.generator.collection.ArrayGenerator;
import org.example.generator.collection.ListGenerator;
import org.example.generator.collection.MapGenerator;
import org.example.generator.object.AbstractTypeGenerator;
import org.example.generator.object.ObjectGenerator;
import org.example.generator.primitive.BooleanGenerator;
import org.example.generator.primitive.ByteGenerator;
import org.example.generator.primitive.CharGenerator;
import org.example.generator.primitive.DoubleGenerator;
import org.example.generator.primitive.FloatGenerator;
import org.example.generator.primitive.IntGenerator;
import org.example.generator.primitive.LongGenerator;
import org.example.generator.primitive.ShortGenerator;
import org.example.generator.simple.EnumGenerator;
import org.example.generator.simple.StringGenerator;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Generator {

    private final Random random;
    private final List<TypeGenerator> generators;

    public Generator() {
        this(new Random());
    }

    public Generator(Random random) {
        this(random, defaultGenerators());
    }

    public Generator(Random random, List<TypeGenerator> generators) {
        this.random = Objects.requireNonNull(random, "random");
        this.generators = List.copyOf(generators);
    }

    public static List<TypeGenerator> defaultGenerators() {
        return List.of(
                new BooleanGenerator(),
                new ByteGenerator(),
                new CharGenerator(),
                new ShortGenerator(),
                new IntGenerator(),
                new LongGenerator(),
                new FloatGenerator(),
                new DoubleGenerator(),
                new StringGenerator(),
                new EnumGenerator(),
                new ArrayGenerator(),
                new ListGenerator(),
                new MapGenerator(),
                new AbstractTypeGenerator(),
                new ObjectGenerator()
        );
    }

    @SuppressWarnings("unchecked")
    public <T> T generate(Class<T> type) {
        Objects.requireNonNull(type, "type");
        return (T) generate(type, 0);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T> T generate(T... typeHint) {
        if (typeHint.length > 0) {
            throw new IllegalArgumentException("generate() вызывается без аргументов, для явного типа есть generate(Class)");
        }
        Class<?> type = typeHint.getClass().getComponentType();
        if (type == Object.class) {
            throw new GenerationException("Не удалось вывести тип: присвойте результат переменной конкретного типа или передайте Class");
        }
        return (T) generate(type);
    }

    Object generate(Type type, int depth) {
        Class<?> raw = Types.rawClass(type);
        for (TypeGenerator generator : generators) {
            if (generator.supports(raw)) {
                return generator.generate(type, new GenerationContext(this, random, depth));
            }
        }
        throw new GenerationException("Тип " + type.getTypeName() + " не поддерживается");
    }
}
