package com.tinkerpop.frames;

import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Predicate;
import com.tinkerpop.blueprints.Query.Compare;

/**
 * GraphQuery that allows framing of results. 
 * 
 * @author Bryn Cooke
 *
 */
public interface FramedGraphQuery extends GraphQuery, FramedQuery {
	@Override
    public FramedGraphQuery has(String key);

    @Override
    public FramedGraphQuery hasNot(String key);

    @Override
    public FramedGraphQuery has(String key, Object value);

    @Override
    public FramedGraphQuery hasNot(String key, Object value);

    @Override
    public FramedGraphQuery has(String key, Predicate predicate, Object value);

    @Override
    @Deprecated
    public <T extends Comparable<T>> FramedGraphQuery has(String key, T value, Compare compare);

    @Override
    public <T extends Comparable<T>> FramedGraphQuery interval(String key, T startValue, T endValue);

    @Override
    public FramedGraphQuery limit(int limit);
}
