package org.example.testclasses;

import org.example.generator.Generatable;

@Generatable
public class Secret {
    private final long code;

    private Secret(long code) {
        this.code = code;
    }

    public long getCode() {
        return code;
    }
}
