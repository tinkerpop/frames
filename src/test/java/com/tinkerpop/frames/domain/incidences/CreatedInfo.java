package com.tinkerpop.frames.domain.incidences;

import com.tinkerpop.frames.EdgeFrame;
import com.tinkerpop.frames.Initial;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.Terminal;
import com.tinkerpop.frames.domain.classes.Person;
import com.tinkerpop.frames.domain.classes.Project;

/**
 * This class frames "created" edges (just like Created and CreatedBy),
 * but uses the {@link Initial} and {@link Terminal} annotations instead
 * of the now deprecated Source/Target.
 */
public interface CreatedInfo extends EdgeFrame {
    @Initial Person getPerson();

    @Terminal Project getProject();

    @Property("weight") public Float getWeight();

    @Property("weight") public void setWeight(float weight);
}
