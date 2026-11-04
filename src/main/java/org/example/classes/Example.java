package org.example.classes;

public class Example {
    int i;
    double d;

    public Example(int i,  double d) {
        this.i = i;
        this.d = d;
    }

    @Override
    public String toString() {
        return "Example(" + i + " " + d + ")" ;
    }
}
