package com.tinkerpop.frames;

import com.tinkerpop.frames.util.Direction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Relation {
    public Class clazz();

    public Direction direction() default Direction.STANDARD;

    public String label() default Tokens.DEFAULT;
}
