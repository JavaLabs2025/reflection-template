package org.example.generator.samples;

import org.example.generator.Generatable;

import java.util.List;
import java.util.Map;

/**
 * Собственные классы для тестов: в генераторе нет ни одной проверки на их имена.
 */
public final class Samples {

    private Samples() {
    }

    public enum Color { RED, GREEN, BLUE }

    public enum NoConstants {}

    public interface Animal {
        String name();
    }

    @Generatable
    public record Cat(String name, int lives) implements Animal {
    }

    @Generatable
    public static class Dog implements Animal {
        private final String name;
        private final Color color;

        public Dog(String name, Color color) {
            this.name = name;
            this.color = color;
        }

        @Override
        public String name() {
            return name;
        }

        public Color color() {
            return color;
        }
    }

    /** Реализует Animal, но без @Generatable: генератор не должен его выбирать. */
    public static class Fish implements Animal {
        public Fish() {
        }

        @Override
        public String name() {
            return "fish";
        }
    }

    public abstract static class Vehicle {
        public abstract int wheels();
    }

    @Generatable
    public static class Bicycle extends Vehicle {
        public Bicycle() {
        }

        @Override
        public int wheels() {
            return 2;
        }
    }

    @Generatable
    public static class Truck extends Vehicle {
        private final int wheels;

        public Truck(int axles) {
            this.wheels = Math.abs(axles) * 2 + 4;
        }

        @Override
        public int wheels() {
            return wheels;
        }
    }

    /** Есть только реализация без аннотации. */
    public interface Plant {
    }

    public static class Tree implements Plant {
        public Tree() {
        }
    }

    @Generatable
    public static class Person {
        private final String name;
        private final int age;
        private final Color color;
        private final Animal pet;
        private final Vehicle vehicle;
        private final List<Integer> scores;
        private final Map<String, Double> attributes;
        private final Color[] favourites;

        public Person(String name, int age, Color color, Animal pet, Vehicle vehicle,
                      List<Integer> scores, Map<String, Double> attributes, Color[] favourites) {
            this.name = name;
            this.age = age;
            this.color = color;
            this.pet = pet;
            this.vehicle = vehicle;
            this.scores = scores;
            this.attributes = attributes;
            this.favourites = favourites;
        }

        public String name() {
            return name;
        }

        public int age() {
            return age;
        }

        public Color color() {
            return color;
        }

        public Animal pet() {
            return pet;
        }

        public Vehicle vehicle() {
            return vehicle;
        }

        public List<Integer> scores() {
            return scores;
        }

        public Map<String, Double> attributes() {
            return attributes;
        }

        public Color[] favourites() {
            return favourites;
        }
    }

    @Generatable
    public static class Account {
        private final String createdBy;

        public Account(String id) {
            this.createdBy = "public";
        }

        @Generatable
        private Account(String id, long balance) {
            this.createdBy = "annotated";
        }

        public String createdBy() {
            return createdBy;
        }
    }

    public static class NotGeneratable {
        public NotGeneratable() {
        }
    }

    @Generatable
    public static class HasNotGeneratable {
        public HasNotGeneratable(NotGeneratable inner) {
        }
    }

    @Generatable
    public static class NoPublicConstructor {
        private NoPublicConstructor() {
        }
    }

    @Generatable
    public static class Faulty {
        public Faulty(int value) {
            throw new IllegalStateException("broken constructor");
        }
    }
}
