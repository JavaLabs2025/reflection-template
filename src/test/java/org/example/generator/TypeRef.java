package org.example.generator;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

abstract class TypeRef<T> {
    final Type type;

    protected TypeRef() {
        this.type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
}
