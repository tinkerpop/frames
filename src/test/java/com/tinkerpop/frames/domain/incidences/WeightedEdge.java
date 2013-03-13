package com.tinkerpop.frames.domain.incidences;

import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Range;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;

/**
 * @author Mike Bryant (https://github.com/mikesname)
 */
public interface WeightedEdge extends EdgeFrame {
    @Property("weight")
    public Float getWeight();

    @Property("weight")
    public void setWeight(float weight);
}
