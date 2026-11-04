package org.example.testclasses;

import org.example.generator.Generatable;

import java.util.List;
import java.util.Map;

@Generatable
public class Library {
    private final String title;
    private final Map<String, List<Point>> shelves;
    private final Color[] colors;
    private final Animal keeper;

    public Library(String title, Map<String, List<Point>> shelves, Color[] colors, Animal keeper) {
        this.title = title;
        this.shelves = shelves;
        this.colors = colors;
        this.keeper = keeper;
    }

    public String getTitle() {
        return title;
    }

    public Map<String, List<Point>> getShelves() {
        return shelves;
    }

    public Color[] getColors() {
        return colors;
    }

    public Animal getKeeper() {
        return keeper;
    }
}
