package org.example.testclasses;

import org.example.generator.Generatable;

@Generatable
public class TwoConstructors {
    private final String source;

    public TwoConstructors(int value) {
        this.source = "plain";
    }

    @Generatable
    public TwoConstructors(String value, double number) {
        this.source = "annotated";
    }

    public String getSource() {
        return source;
    }
}
