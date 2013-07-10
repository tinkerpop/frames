package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Predicate;
import com.tinkerpop.blueprints.Query;


/**
 * Query that allows framing of results 
 * @author Bryn Cooke
 *
 */
public interface FramedQuery extends Query {

	
    /**
     * Filter out elements that do not have a property with provided key.
     *
     * @param key the key of the property
     * @return the modified query object
     */
    public FramedQuery has(String key);

    /**
     * Filter out elements that have a property with provided key.
     *
     * @param key the key of the property
     * @return the modified query object
     */
    public FramedQuery hasNot(String key);

    /**
     * Filter out elements that do not have a property value equal to provided value.
     *
     * @param key   the key of the property
     * @param value the value to check against
     * @return the modified query object
     */
    public FramedQuery has(String key, Object value);

    /**
     * Filter out elements that have a property value equal to provided value.
     *
     * @param key   the key of the property
     * @param value the value to check against
     * @return the modified query object
     */
    public FramedQuery hasNot(String key, Object value);

    /**
     * Filter out the element if it does not have a property with a comparable value.
     *
     * @param key     the key of the property
     * @param predicate the comparator to use for comparison
     * @param value  the value to check against
     * @return the modified query object
     */
    public FramedQuery has(String key, Predicate predicate, Object value);

    /**
     * Filter out the element if it does not have a property with a comparable value.
     *
     * @param key     the key of the property
     * @param value   the value to check against
     * @param compare the comparator to use for comparison
     * @return the modified query object
     */
    @Deprecated
    public <T extends Comparable<T>> FramedQuery has(String key, T value, Compare compare);

    /**
     * Filter out the element of its property value is not within the provided interval.
     *
     * @param key        the key of the property
     * @param startValue the inclusive start value of the interval
     * @param endValue   the exclusive end value of the interval
     * @return the modified query object
     */
    public <T extends Comparable<T>> FramedQuery interval(String key, T startValue, T endValue);

    /**
     * Filter out the element if the take number of incident/adjacent elements to retrieve has already been reached.
     *
     * @param limit the take number of elements to return
     * @return the modified query object
     */
    public FramedQuery limit(int limit);

    /**
     * Execute the query and return the matching edges.
     *
     * @param the default annotated interface to frame the edge as
     * @return the unfiltered incident edges
     */
    public <T> Iterable<T> edges(Class<T> kind);

    /**
     * Execute the query and return the vertices on the other end of the matching edges.
     *
     * @param the default annotated interface to frame the vertex as
     * @return the unfiltered adjacent vertices
     */
	public <T> Iterable<T> vertices(Class<T> kind);
	
}
