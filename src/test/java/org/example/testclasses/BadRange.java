package org.example.testclasses;

import org.example.generator.Generatable;
import org.example.generator.Range;

@Generatable
public class BadRange {
    public BadRange(@Range(min = 0, max = 10) String text) {
    }
}
