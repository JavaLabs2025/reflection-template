package org.example.generator.primitive;

import java.util.Random;

public class CharGenerator extends PrimitiveGenerator<Character> {

    public CharGenerator() {
        super(char.class, Character.class);
    }

    @Override
    protected Character next(Random random) {
        return (char) random.nextInt('a', 'z' + 1);
    }
}
