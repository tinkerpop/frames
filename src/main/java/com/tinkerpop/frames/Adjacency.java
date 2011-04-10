package com.tinkerpop.frames;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Adjacency annotate getters and adders to represent a Vertex adjacent to an Edge.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Adjacency {

    /**
     * The labels of the edges that are adjacent to the vertex.
     *
     * @return the edge label
     */
    public String label();

    /**
     * The direction of the edges (incoming/inverse or outgoing/standard).
     *
     * @return the edge direction
     */
    public Direction direction() default Direction.STANDARD;
}
