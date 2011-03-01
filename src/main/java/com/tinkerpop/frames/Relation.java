package com.tinkerpop.frames;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Relation {

    public static enum Type {
        HALF, FULL
    }

    public static enum Direction {
        STANDARD, INVERSE
    }

    public Type type();

    public String label();

    public Direction direction();
}
