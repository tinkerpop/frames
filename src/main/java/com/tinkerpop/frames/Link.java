package com.tinkerpop.frames;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tinkerpop.blueprints.Direction;

/**
 * Returns the vertex linked with the frame.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Link {
	 /**
     * The linking vertex direction where:
     * <pre>
     * @Link(Direction.OUT) = {@link Edge.getVertex(Direction.OUT)}
     * @Link(Direction.IN) = {@link Edge.getVertex(Direction.IN)}
     *</pre>
     * @return the edge direction
     */
    public Direction value();
}