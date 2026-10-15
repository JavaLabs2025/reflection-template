package org.example.generator.object;

import org.example.generator.GenerationException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.stream.Stream;

final class ImplementationScanner {

    private static final String CLASS_SUFFIX = ".class";

    private ImplementationScanner() {
    }

    static List<Class<?>> findImplementations(Class<?> abstractType) {
        ClassLoader loader = abstractType.getClassLoader();
        if (loader == null) {
            return List.of();
        }
        String packagePath = abstractType.getPackageName().replace('.', '/');
        List<Class<?>> result = new ArrayList<>();
        try {
            for (URL url : Collections.list(loader.getResources(packagePath))) {
                for (String resource : classResources(url, packagePath)) {
                    Class<?> candidate = load(resource, loader);
                    if (candidate != null && isImplementation(abstractType, candidate)) {
                        result.add(candidate);
                    }
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new GenerationException("Не удалось просканировать пакет " + abstractType.getPackageName(), e);
        }
        return result;
    }

    private static List<String> classResources(URL url, String packagePath) throws IOException, URISyntaxException {
        return switch (url.getProtocol()) {
            case "file" -> fromDirectory(Path.of(url.toURI()), packagePath);
            case "jar" -> fromJar((JarURLConnection) url.openConnection(), packagePath);
            default -> List.of();
        };
    }

    private static List<String> fromDirectory(Path directory, String packagePath) throws IOException {
        String prefix = packagePath.isEmpty() ? "" : packagePath + "/";
        try (Stream<Path> files = Files.walk(directory)) {
            return files
                    .map(file -> directory.relativize(file).toString().replace(File.separatorChar, '/'))
                    .filter(name -> name.endsWith(CLASS_SUFFIX))
                    .map(name -> prefix + name)
                    .toList();
        }
    }

    private static List<String> fromJar(JarURLConnection connection, String packagePath) throws IOException {
        String prefix = packagePath.isEmpty() ? "" : packagePath + "/";
        return connection.getJarFile().stream()
                .map(JarEntry::getName)
                .filter(name -> name.startsWith(prefix) && name.endsWith(CLASS_SUFFIX))
                .toList();
    }

    private static Class<?> load(String resource, ClassLoader loader) {
        String className = resource.substring(0, resource.length() - CLASS_SUFFIX.length()).replace('/', '.');
        try {
            return Class.forName(className, false, loader);
        } catch (ClassNotFoundException | LinkageError e) {
            return null; // module-info, package-info и классы с недоступными зависимостями
        }
    }

    private static boolean isImplementation(Class<?> abstractType, Class<?> candidate) {
        return abstractType.isAssignableFrom(candidate)
                && !candidate.isInterface()
                && !Modifier.isAbstract(candidate.getModifiers());
    }
}
