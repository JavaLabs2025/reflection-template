package org.example.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Generator {
    static final int MAX_DEPTH = 5;
    static final int MAX_ATTEMPTS = 1000;
    static final int MIN_SIZE = 1;
    static final int MAX_SIZE = 5;
    static final int MIN_NUMBER = -1000;
    static final int MAX_NUMBER = 1000;
    static final int MAX_STRING_LENGTH = 10;
    static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final Random random;
    private final Map<Class<?>, Supplier<Object>> simpleValues = new HashMap<>();
    private final Map<Class<?>, List<Class<?>>> implementations = new HashMap<>();

    public Generator() {
        this(new Random());
    }

    public Generator(long seed) {
        this(new Random(seed));
    }

    private Generator(Random random) {
        this.random = random;
        register(() -> random.nextInt(MIN_NUMBER, MAX_NUMBER + 1), int.class, Integer.class);
        register(() -> random.nextLong(MIN_NUMBER, MAX_NUMBER + 1), long.class, Long.class);
        register(() -> (short) random.nextInt(MIN_NUMBER, MAX_NUMBER + 1), short.class, Short.class);
        register(() -> (byte) random.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1), byte.class, Byte.class);
        register(() -> random.nextDouble(MIN_NUMBER, MAX_NUMBER), double.class, Double.class);
        register(() -> random.nextFloat(MIN_NUMBER, MAX_NUMBER), float.class, Float.class);
        register(random::nextBoolean, boolean.class, Boolean.class);
        register(this::randomChar, char.class, Character.class);
        register(this::randomString, String.class);
    }

    private void register(Supplier<Object> supplier, Class<?>... types) {
        for (Class<?> type : types) {
            simpleValues.put(type, supplier);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T generateValueOfType(Class<T> type) {
        return (T) generate(type, 0);
    }

    private Object generate(Type type, int depth) {
        return switch (type) {
            case Class<?> c -> generateClass(c, depth);
            case ParameterizedType p -> generateParameterized(p, depth);
            case WildcardType w -> generate(wildcardBound(w), depth);
            case null, default -> throw new GenerationException("Unsupported type: " + type);
        };
    }

    private Object generateClass(Class<?> type, int depth) {
        if (type == void.class || type == Void.class) throw new GenerationException("Cannot generate void");
        Supplier<Object> simple = simpleValues.get(type);
        if (simple != null) return simple.get();
        if (type.isEnum()) return randomEnumConstant(type);
        if (type.isArray()) return randomArray(type.getComponentType(), depth);
        if (type == List.class) return randomList(Integer.class, depth);
        if (type == Map.class) return randomMap(String.class, Integer.class, depth);
        return randomObject(type, depth);
    }

    private Object generateParameterized(ParameterizedType type, int depth) {
        Type raw = type.getRawType();
        Type[] args = type.getActualTypeArguments();
        if (raw == List.class) return randomList(args[0], depth);
        if (raw == Map.class) return randomMap(args[0], args[1], depth);
        return generateClass((Class<?>) raw, depth);
    }

    private Type wildcardBound(WildcardType type) {
        Type[] lowerBounds = type.getLowerBounds();
        return lowerBounds.length > 0 ? lowerBounds[0] : type.getUpperBounds()[0];
    }

    private char randomChar() {
        return CHARS.charAt(random.nextInt(CHARS.length()));
    }

    private String randomString() {
        int length = random.nextInt(1, MAX_STRING_LENGTH + 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(randomChar());
        }
        return sb.toString();
    }

    private Object randomEnumConstant(Class<?> type) {
        Object[] constants = type.getEnumConstants();
        if (constants.length == 0) {
            throw new GenerationException("Enum " + type.getName() + " has no constants");
        }
        return constants[random.nextInt(constants.length)];
    }

    private Object randomArray(Class<?> elementType, int depth) {
        int length = randomSize(depth);
        Object array = Array.newInstance(elementType, length);
        for (int i = 0; i < length; i++) {
            Array.set(array, i, generate(elementType, depth));
        }
        return array;
    }

    private List<Object> randomList(Type elementType, int depth) {
        int size = randomSize(depth);
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(generate(elementType, depth));
        }
        return list;
    }

    private Map<Object, Object> randomMap(Type keyType, Type valueType, int depth) {
        int size = randomSize(depth);
        Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(generate(keyType, depth), generate(valueType, depth));
        }
        return map;
    }

    private int randomSize(int depth) {
        return depth > MAX_DEPTH ? 0 : random.nextInt(MIN_SIZE, MAX_SIZE + 1);
    }

    private Object randomObject(Class<?> type, int depth) {
        if (!type.isAnnotationPresent(Generatable.class)) {
            throw new GenerationException(type.getName() + " is not annotated with @Generatable");
        }
        if (depth > MAX_DEPTH) {
            return null;
        }
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            return generate(randomImplementation(type), depth);
        }
        return construct(type, depth);
    }

    private Object construct(Class<?> type, int depth) {
        Constructor<?>[] constructors = type.getConstructors();
        if (constructors.length == 0) {
            throw new GenerationException(type.getName() + " has no public constructors");
        }
        for (int attempt = 1; ; attempt++) {
            Constructor<?> constructor = constructors[random.nextInt(constructors.length)];
            Object[] args = randomArguments(constructor, depth + 1);
            try {
                return constructor.newInstance(args);
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (!(cause instanceof IllegalArgumentException)) {
                    throw new GenerationException("Constructor of " + type.getName() + " threw " + cause, cause);
                }
                if (attempt == MAX_ATTEMPTS) {
                    throw new GenerationException("Constructor of " + type.getName() + " rejected arguments "
                            + MAX_ATTEMPTS + " times, last error: " + cause.getMessage(), cause);
                }
            } catch (ReflectiveOperationException | IllegalArgumentException e) {
                throw new GenerationException("Cannot create " + type.getName(), e);
            }
        }
    }

    private Object[] randomArguments(Constructor<?> constructor, int depth) {
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Range range = parameter.getAnnotation(Range.class);
            try {
                args[i] = range != null
                        ? randomInRange(parameter.getType(), range)
                        : generate(parameter.getParameterizedType(), depth);
            } catch (GenerationException e) {
                throw new GenerationException("Cannot generate parameter '" + parameter.getName() + "' of "
                        + constructor.getDeclaringClass().getName() + ": " + e.getMessage(), e);
            }
        }
        return args;
    }

    private Object randomInRange(Class<?> type, Range range) {
        if (range.min() >= range.max()) {
            throw new GenerationException("Invalid @Range: min " + range.min() + " is not less than max " + range.max());
        }
        if (type == double.class || type == Double.class) return random.nextDouble(range.min(), range.max());
        if (type == float.class || type == Float.class) return random.nextFloat(range.min(), range.max());
        long value = random.nextLong(range.min(), range.max() + 1);
        if (type == long.class || type == Long.class) return value;
        if (type == int.class || type == Integer.class) return (int) value;
        if (type == short.class || type == Short.class) return (short) value;
        if (type == byte.class || type == Byte.class) return (byte) value;
        throw new GenerationException("@Range is not supported for " + type.getName());
    }

    private Class<?> randomImplementation(Class<?> type) {
        List<Class<?>> candidates = implementations.computeIfAbsent(type, this::findImplementations);
        if (candidates.isEmpty()) {
            throw new GenerationException("No @Generatable implementations of " + type.getName()
                    + " in package " + type.getPackageName() + " and its subpackages");
        }
        return candidates.get(random.nextInt(candidates.size()));
    }

    private List<Class<?>> findImplementations(Class<?> type) {
        String packageName = type.getPackageName();
        ClassLoader loader = type.getClassLoader();
        List<Class<?>> result = new ArrayList<>();
        try {
            Enumeration<URL> roots = loader.getResources(packageName.replace('.', '/'));
            while (roots.hasMoreElements()) {
                Path root = Path.of(roots.nextElement().toURI());
                try (Stream<Path> files = Files.walk(root)) {
                    for (Path file : files.toList()) {
                        String path = root.relativize(file).toString();
                        if (!path.endsWith(".class")) {
                            continue;
                        }
                        String className = packageName + "."
                                + path.substring(0, path.length() - ".class".length()).replace(File.separatorChar, '.');
                        Class<?> candidate = Class.forName(className, false, loader);
                        boolean concrete = !candidate.isInterface() && !Modifier.isAbstract(candidate.getModifiers());
                        if (concrete && type.isAssignableFrom(candidate) && candidate.isAnnotationPresent(Generatable.class)) {
                            result.add(candidate);
                        }
                    }
                }
            }
        } catch (IOException | URISyntaxException | ClassNotFoundException e) {
            throw new GenerationException("Cannot scan package " + packageName, e);
        }
        result.sort(Comparator.comparing(Class::getName));
        return result;
    }
}
