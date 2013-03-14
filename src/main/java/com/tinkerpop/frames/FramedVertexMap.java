package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedVertexMap<T extends VertexFrame> implements Map<T, Object> {

    private final Map<Vertex, Object> map;
    protected final Class<T> kind;
    protected final FramedGraph<? extends Graph> framedGraph;

    public FramedVertexMap(final FramedGraph<? extends Graph> framedGraph, final Map<Vertex, Object> map, final Class<T> kind) {
        this.framedGraph = framedGraph;
        this.map = map;
        this.kind = kind;
    }

    public Object get(final Object key) {
        return this.map.get(((T) key).asVertex());
    }

    public Object remove(final Object key) {
        return this.map.remove(((T) key).asVertex());
    }

    public int size() {
        return this.map.size();
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public Object put(final T key, final Object value) {
        return this.map.put(key.asVertex(), value);
    }

    public void putAll(final Map otherMap) {
        for (final Entry<T, Object> entry : (Set<Entry<T, Object>>) otherMap.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    public boolean containsValue(final Object value) {
        return this.map.containsValue(value);
    }

    public boolean containsKey(final Object key) {
        return this.map.containsKey(((T) key).asVertex());
    }

    public Set<T> keySet() {
        final Set<T> keys = new HashSet<T>();
        for (final Vertex v : this.map.keySet()) {
            keys.add(this.framedGraph.frame(v, this.kind));
        }
        return keys;
    }

    public Collection<Object> values() {
        return this.map.values();
    }

    public Set<Entry<T, Object>> entrySet() {
        final Set<Entry<T, Object>> entries = new HashSet<Entry<T, Object>>();
        for (final Entry<Vertex, Object> entry : this.map.entrySet()) {
            entries.add(new FramedEntry<T>(entry));
        }
        return entries;
    }

    public void clear() {
        this.map.clear();
    }

    private class FramedEntry<T> implements Entry<T, Object> {

        private final Entry<Vertex, Object> entry;

        public FramedEntry(final Entry<Vertex, Object> entry) {
            this.entry = entry;
        }

        public Object setValue(final Object object) {
            return this.entry.setValue(object);
        }

        public Object getValue() {
            return entry.getValue();
        }

        public T getKey() {
            return (T) framedGraph.frame(entry.getKey(), kind);
        }
    }
}
