package org.example.testclasses;

import org.example.classes.Shape;
import org.example.generator.Generatable;

import java.util.List;
import java.util.Map;

@Generatable
public class Drawing {
    private final List<? extends Shape> shapes;
    private final Map<Color, Shape[]> layers;

    public Drawing(List<? extends Shape> shapes, Map<Color, Shape[]> layers) {
        this.shapes = shapes;
        this.layers = layers;
    }

    public List<? extends Shape> getShapes() {
        return shapes;
    }

    public Map<Color, Shape[]> getLayers() {
        return layers;
    }
}
