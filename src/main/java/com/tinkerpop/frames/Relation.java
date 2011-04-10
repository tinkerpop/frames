package com.tinkerpop.frames;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Relations annotate getters and adders to represent a Vertex adjacent to a Vertex.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Relation {
    /**
     * The label of the edges making the relation between the vertices.
     *
     * @return the edge label
     */
    public String label();

    /**
     * The edge direction of the relation (incoming/inverse, outgoing/standard).
     *
     * @return the direction of the edges composing the relation
     */

    public Direction direction() default Direction.STANDARD;
}
