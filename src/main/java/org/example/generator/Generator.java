package org.example.generator;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

public class Generator {
    public static final String DEFAULT_SCAN_PACKAGE = "org.example";
    public static final int MAX_DEPTH = 5;
    public static final int MAX_SIZE = 5;
    public static final int MAX_STRING_LENGTH = 10;
    public static final int NUMBER_BOUND = 1000;
    public static final int MAX_INVARIANT_ATTEMPTS = 100;

    private static final Type DEFAULT_ELEMENT_TYPE = Integer.class;
    private static final Type DEFAULT_KEY_TYPE = String.class;
    private static final Type DEFAULT_VALUE_TYPE = Integer.class;

    private static final Map<Class<?>, Class<?>> WRAPPERS = Map.of(
            Boolean.class, boolean.class,
            Byte.class, byte.class,
            Short.class, short.class,
            Character.class, char.class,
            Integer.class, int.class,
            Long.class, long.class,
            Float.class, float.class,
            Double.class, double.class
    );

    private static final Map<Class<?>, Function<Random, Object>> PRIMITIVES = Map.of(
            boolean.class, Random::nextBoolean,
            byte.class, r -> (byte) r.nextInt(Byte.MIN_VALUE, Byte.MAX_VALUE + 1),
            short.class, r -> (short) r.nextInt(-NUMBER_BOUND, NUMBER_BOUND + 1),
            char.class, r -> (char) r.nextInt('a', 'z' + 1),
            int.class, r -> r.nextInt(-NUMBER_BOUND, NUMBER_BOUND + 1),
            long.class, r -> r.nextLong(-NUMBER_BOUND, NUMBER_BOUND + 1),
            float.class, r -> r.nextFloat(-NUMBER_BOUND, NUMBER_BOUND),
            double.class, r -> r.nextDouble(-NUMBER_BOUND, NUMBER_BOUND)
    );

    private final Random random;
    private final ImplementationFinder implementationFinder;

    public Generator() {
        this(new Random());
    }

    public Generator(Random random) {
        this(random, DEFAULT_SCAN_PACKAGE);
    }

    public Generator(Random random, String... scanPackages) {
        this.random = Objects.requireNonNull(random, "random");
        this.implementationFinder = new ImplementationFinder(scanPackages);
    }

    @SuppressWarnings("unchecked")
    public <T> T generateValueOfType(Class<T> type) {
        return (T) generateValueOfType((Type) type);
    }

    public Object generateValueOfType(Type type) {
        Objects.requireNonNull(type, "type");
        return generate(type, 0);
    }

    private Object generate(Type type, int depth) {
        return switch (type) {
            case Class<?> c -> generateClass(c, depth);
            case ParameterizedType p -> generateParameterized(p, depth);
            case GenericArrayType a -> generateArray(a.getGenericComponentType(), depth);
            case WildcardType w -> generate(boundOf(w), depth);
            case TypeVariable<?> v -> generate(v.getBounds()[0], depth);
            default -> throw new GenerationException("Unsupported type: " + type.getTypeName());
        };
    }

    private Object generateClass(Class<?> type, int depth) {
        if (type == void.class || type == Void.class) {
            throw new GenerationException("Cannot generate a value of type void");
        }
        if (isPrimitiveOrWrapper(type)) {
            return generatePrimitive(type);
        }
        if (type == String.class) {
            return generateString();
        }
        if (type.isEnum()) {
            return generateEnum(type);
        }
        if (type.isArray()) {
            return generateArray(type.getComponentType(), depth);
        }
        if (isList(type)) {
            return generateList(DEFAULT_ELEMENT_TYPE, depth);
        }
        if (isMap(type)) {
            return generateMap(DEFAULT_KEY_TYPE, DEFAULT_VALUE_TYPE, depth);
        }
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            return generateClass(chooseImplementation(type), depth);
        }
        return generateObject(type, depth);
    }

    private Object generateParameterized(ParameterizedType type, int depth) {
        Class<?> raw = (Class<?>) type.getRawType();
        Type[] arguments = type.getActualTypeArguments();
        if (isList(raw)) {
            return generateList(arguments[0], depth);
        }
        if (isMap(raw)) {
            return generateMap(arguments[0], arguments[1], depth);
        }
        throw new GenerationException("Parameterized type " + type.getTypeName()
                + " is not supported: only List and Map can be parameterized");
    }

    private Object generatePrimitive(Class<?> type) {
        Class<?> primitive = type.isPrimitive() ? type : WRAPPERS.get(type);
        return PRIMITIVES.get(primitive).apply(random);
    }

    private String generateString() {
        int length = random.nextInt(1, MAX_STRING_LENGTH + 1);
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append((char) random.nextInt('a', 'z' + 1));
        }
        return builder.toString();
    }

    private Object generateEnum(Class<?> type) {
        Object[] constants = type.getEnumConstants();
        if (constants.length == 0) {
            throw new GenerationException("Enum " + type.getName() + " has no constants");
        }
        return constants[random.nextInt(constants.length)];
    }

    private Object generateArray(Type componentType, int depth) {
        int length = sizeAt(depth);
        Object array = Array.newInstance(rawClass(componentType), length);
        for (int i = 0; i < length; i++) {
            Array.set(array, i, generate(componentType, depth + 1));
        }
        return array;
    }

    private List<Object> generateList(Type elementType, int depth) {
        int size = sizeAt(depth);
        List<Object> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(generate(elementType, depth + 1));
        }
        return list;
    }

    private Map<Object, Object> generateMap(Type keyType, Type valueType, int depth) {
        int size = sizeAt(depth);
        Map<Object, Object> map = new HashMap<>();
        for (int i = 0; i < size; i++) {
            map.put(generate(keyType, depth + 1), generate(valueType, depth + 1));
        }
        return map;
    }

    private int sizeAt(int depth) {
        return depth >= MAX_DEPTH ? 0 : random.nextInt(1, MAX_SIZE + 1);
    }

    private Class<?> chooseImplementation(Class<?> type) {
        List<Class<?>> implementations = implementationFinder.findImplementations(type);
        if (implementations.isEmpty()) {
            throw new GenerationException("No @Generatable implementations of " + type.getName()
                    + " found in packages " + implementationFinder.packages());
        }
        return implementations.get(random.nextInt(implementations.size()));
    }

    private Object generateObject(Class<?> type, int depth) {
        requireGeneratable(type);
        Constructor<?> constructor = chooseConstructor(type);
        List<Method> invariants = findInvariants(type);
        for (int attempt = 0; attempt < MAX_INVARIANT_ATTEMPTS; attempt++) {
            Object instance = instantiate(constructor, depth);
            if (satisfiesInvariants(instance, invariants)) {
                return instance;
            }
        }
        throw new GenerationException("Failed to generate " + type.getName() + " satisfying its @Invariant methods in "
                + MAX_INVARIANT_ATTEMPTS + " attempts");
    }

    private static void requireGeneratable(Class<?> type) {
        if (!type.isAnnotationPresent(Generatable.class)) {
            throw new GenerationException(type.getName() + " is not annotated with @Generatable");
        }
        if (type.getEnclosingClass() != null && !Modifier.isStatic(type.getModifiers())) {
            throw new GenerationException("Inner, local and anonymous classes are not supported: " + type.getName());
        }
    }

    private Constructor<?> chooseConstructor(Class<?> type) {
        Constructor<?>[] declared = type.getDeclaredConstructors();
        List<Constructor<?>> marked = Arrays.stream(declared)
                .filter(constructor -> constructor.isAnnotationPresent(Generatable.class))
                .toList();
        if (marked.size() > 1) {
            throw new GenerationException(type.getName() + " has more than one @Generatable constructor");
        }
        if (marked.size() == 1) {
            return marked.getFirst();
        }
        List<Constructor<?>> publicConstructors = Arrays.stream(declared)
                .filter(constructor -> Modifier.isPublic(constructor.getModifiers()))
                .toList();
        List<Constructor<?>> candidates = publicConstructors.isEmpty() ? Arrays.asList(declared) : publicConstructors;
        return candidates.get(random.nextInt(candidates.size()));
    }

    private Object instantiate(Constructor<?> constructor, int depth) {
        Parameter[] parameters = constructor.getParameters();
        Object[] arguments = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            arguments[i] = generateArgument(parameters[i], depth + 1);
        }
        String owner = constructor.getDeclaringClass().getName();
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(arguments);
        } catch (InvocationTargetException e) {
            throw new GenerationException("Constructor of " + owner + " threw " + e.getCause(), e.getCause());
        } catch (ReflectiveOperationException | RuntimeException e) {
            throw new GenerationException("Cannot invoke constructor of " + owner, e);
        }
    }

    private Object generateArgument(Parameter parameter, int depth) {
        Type type = parameter.getParameterizedType();
        Range range = parameter.getAnnotation(Range.class);
        if (range != null) {
            return generateInRange(type, range);
        }
        if (depth >= MAX_DEPTH && isObjectType(type)) {
            return null;
        }
        return generate(type, depth);
    }

    private Object generateInRange(Type type, Range range) {
        if (!(type instanceof Class<?> c) || !isPrimitiveOrWrapper(c) || c == boolean.class || c == Boolean.class
                || c == char.class || c == Character.class) {
            throw new GenerationException("@Range is applicable only to numeric parameters, but found " + type.getTypeName());
        }
        if (range.min() > range.max()) {
            throw new GenerationException("Invalid @Range: min " + range.min() + " is greater than max " + range.max());
        }
        Class<?> primitive = c.isPrimitive() ? c : WRAPPERS.get(c);
        if (primitive == double.class || primitive == float.class) {
            double value = range.min() == range.max() ? range.min() : random.nextDouble(range.min(), range.max());
            if (primitive == float.class) {
                return (float) value;
            }
            return value;
        }
        long low = (long) Math.ceil(range.min());
        long high = (long) Math.floor(range.max());
        if (low > high || toIntegral(primitive, low).longValue() != low || toIntegral(primitive, high).longValue() != high) {
            throw new GenerationException("@Range [" + range.min() + ", " + range.max() + "] has no values of type " + primitive);
        }
        return toIntegral(primitive, random.nextLong(low, high + 1));
    }

    private static Number toIntegral(Class<?> primitive, long value) {
        if (primitive == byte.class) {
            return (byte) value;
        }
        if (primitive == short.class) {
            return (short) value;
        }
        if (primitive == int.class) {
            return (int) value;
        }
        return value;
    }

    private static List<Method> findInvariants(Class<?> type) {
        List<Method> invariants = new ArrayList<>();
        for (Class<?> current = type; current != null; current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Invariant.class)) {
                    continue;
                }
                if (Modifier.isStatic(method.getModifiers()) || method.getParameterCount() != 0
                        || method.getReturnType() != boolean.class) {
                    throw new GenerationException("@Invariant method " + method
                            + " must be a non-static method without parameters returning boolean");
                }
                method.setAccessible(true);
                invariants.add(method);
            }
        }
        return invariants;
    }

    private static boolean satisfiesInvariants(Object instance, List<Method> invariants) {
        for (Method invariant : invariants) {
            try {
                if (!(boolean) invariant.invoke(instance)) {
                    return false;
                }
            } catch (InvocationTargetException e) {
                throw new GenerationException("@Invariant method " + invariant.getName() + " threw " + e.getCause(), e.getCause());
            } catch (ReflectiveOperationException e) {
                throw new GenerationException("Cannot invoke @Invariant method " + invariant.getName(), e);
            }
        }
        return true;
    }

    private static boolean isObjectType(Type type) {
        Class<?> raw = rawClass(type);
        return !(isPrimitiveOrWrapper(raw) || raw == String.class || raw.isEnum() || raw.isArray()
                || isList(raw) || isMap(raw));
    }

    private static boolean isPrimitiveOrWrapper(Class<?> type) {
        return type.isPrimitive() || WRAPPERS.containsKey(type);
    }

    private static boolean isList(Class<?> type) {
        return type == List.class || type == Collection.class || type == Iterable.class || type == ArrayList.class;
    }

    private static boolean isMap(Class<?> type) {
        return type == Map.class || type == HashMap.class;
    }

    private static Class<?> rawClass(Type type) {
        return switch (type) {
            case Class<?> c -> c;
            case ParameterizedType p -> (Class<?>) p.getRawType();
            case GenericArrayType a -> rawClass(a.getGenericComponentType()).arrayType();
            case WildcardType w -> rawClass(boundOf(w));
            case TypeVariable<?> v -> rawClass(v.getBounds()[0]);
            default -> throw new GenerationException("Unsupported type: " + type.getTypeName());
        };
    }

    private static Type boundOf(WildcardType type) {
        Type[] lowerBounds = type.getLowerBounds();
        return lowerBounds.length > 0 ? lowerBounds[0] : type.getUpperBounds()[0];
    }
}
