package com.tinkerpop.frames.example;

import com.tinkerpop.frames.Domain;
import com.tinkerpop.frames.Range;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface CreatedBy {

    @Domain
    public Project getDomain();

    @Range
    public Person getRange();
}
