package com.tinkerpop.frames;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The target of the adjacency.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * 
 * @deprecated Use {@link Link} instead.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Range {
}