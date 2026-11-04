package org.example.testclasses;

import org.example.generator.Generatable;
import org.example.generator.Range;

@Generatable
public record Dice(@Range(min = 1, max = 6) int value) {
}
