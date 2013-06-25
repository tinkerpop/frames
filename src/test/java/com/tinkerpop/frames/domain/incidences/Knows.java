package com.tinkerpop.frames.domain.incidences;

import com.tinkerpop.frames.Initial;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Terminal;
import com.tinkerpop.frames.domain.classes.Person;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Knows {

    @Property("weight")
    public Float getWeight();

    @Property("weight")
    public Float setWeight(float weight);

    @Initial
    public Person getInitial();

    @Terminal
    public Person getTerminal();
}
