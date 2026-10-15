package org.example.generator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * На классе: генератору разрешено создавать экземпляры этого класса,
 * в том числе как реализацию запрошенного интерфейса или абстрактного класса.
 * На конструкторе: генератор выбирает только среди помеченных конструкторов (даже не публичных).
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.CONSTRUCTOR})
public @interface Generatable {
}
