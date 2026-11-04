package org.example.generator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
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
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

final class ImplementationFinder {
    private final List<String> packages;
    private final Map<Class<?>, List<Class<?>>> cache = new HashMap<>();
    private List<Class<?>> classes;

    ImplementationFinder(String... packages) {
        this.packages = List.of(packages);
    }

    List<String> packages() {
        return packages;
    }

    List<Class<?>> findImplementations(Class<?> base) {
        return cache.computeIfAbsent(base, type -> classes().stream()
                .filter(type::isAssignableFrom)
                .filter(candidate -> !candidate.isInterface() && !Modifier.isAbstract(candidate.getModifiers()))
                .filter(candidate -> candidate.isAnnotationPresent(Generatable.class))
                .toList());
    }

    private List<Class<?>> classes() {
        if (classes == null) {
            Set<Class<?>> found = new TreeSet<>(Comparator.comparing(Class::getName));
            for (String packageName : packages) {
                found.addAll(scan(packageName));
            }
            classes = List.copyOf(found);
        }
        return classes;
    }

    private static List<Class<?>> scan(String packageName) {
        ClassLoader loader = classLoader();
        List<Class<?>> result = new ArrayList<>();
        try {
            Enumeration<URL> resources = loader.getResources(packageName.replace('.', '/'));
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                if ("file".equals(url.getProtocol())) {
                    result.addAll(scanDirectory(Path.of(url.toURI()), packageName, loader));
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new GenerationException("Failed to scan package " + packageName, e);
        }
        return result;
    }

    private static List<Class<?>> scanDirectory(Path root, String packageName, ClassLoader loader) throws IOException {
        try (Stream<Path> files = Files.walk(root)) {
            return files
                    .filter(file -> file.toString().endsWith(".class"))
                    .map(file -> toClassName(root, file, packageName))
                    .filter(name -> !name.endsWith("module-info") && !name.endsWith("package-info"))
                    .map(name -> load(name, loader))
                    .flatMap(Optional::stream)
                    .toList();
        }
    }

    private static String toClassName(Path root, Path file, String packageName) {
        String relative = root.relativize(file).toString();
        String simpleName = relative.substring(0, relative.length() - ".class".length())
                .replace(File.separatorChar, '.');
        return packageName.isEmpty() ? simpleName : packageName + "." + simpleName;
    }

    private static Optional<Class<?>> load(String name, ClassLoader loader) {
        try {
            return Optional.of(Class.forName(name, false, loader));
        } catch (ClassNotFoundException | LinkageError e) {
            return Optional.empty();
        }
    }

    private static ClassLoader classLoader() {
        ClassLoader context = Thread.currentThread().getContextClassLoader();
        return context != null ? context : ImplementationFinder.class.getClassLoader();
    }
}
