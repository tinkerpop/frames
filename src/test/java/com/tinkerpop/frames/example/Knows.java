package com.tinkerpop.frames.example;

import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Range;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Knows {

    @Property("weight")
    public Float getWeight();

    @Domain
    public Person getDomain();

    @Range
    public Person getRange();
}
