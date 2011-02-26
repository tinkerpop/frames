package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.util.Relations;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FrameManager {

    private Graph graph;

    public FrameManager(final Graph graph) {
        this.graph = graph;
    }

    public <T> T load(final Class<T> clazz, final Vertex vertex) throws Exception {
        final T object = clazz.getConstructor().newInstance();
        for (final Field field : clazz.getDeclaredFields()) {
            final boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            if (null != field.getAnnotation(Element.class)) {
                field.set(object, vertex);
            } else if (null != field.getAnnotation(Property.class)) {
                field.set(object, vertex.getProperty(field.getName()));
            } else if (null != field.getAnnotation(Relation.class)) {
                Set<Vertex> relatedVertices = new HashSet<Vertex>();
                for (Edge edge : vertex.getOutEdges(field.getName())) {
                    relatedVertices.add(edge.getInVertex());
                }
                field.set(object, new Relations<T>(this, relatedVertices, field.getAnnotation(Relation.class).clazz()));
            }
            field.setAccessible(isAccessible);
        }
        return object;
    }

    public <T> T load(final Class<T> clazz, final Object id) throws Exception {
        if (id instanceof com.tinkerpop.blueprints.pgm.Element) {
            return this.load(clazz, id);
        }
        final Vertex vertex = this.graph.getVertex(id);
        return this.load(clazz, vertex);
    }

    public <T> void save(T object) throws Exception {

        Field vertexField = FrameManager.getAnnotatedField(object.getClass(), Element.class);
        boolean isAccessible = vertexField.isAccessible();
        vertexField.setAccessible(true);
        Vertex vertex = (Vertex) vertexField.get(object);
        vertexField.setAccessible(isAccessible);

        for (Field field : object.getClass().getDeclaredFields()) {
            isAccessible = field.isAccessible();
            field.setAccessible(true);

            if (null != field.getAnnotation(Property.class)) {
                vertex.setProperty(field.getName(), field.get(object));
            } else if (null != field.getAnnotation(Relation.class)) {
                // do something smart here smart guy
            }

            field.setAccessible(isAccessible);
        }
    }

    private static Field getAnnotatedField(Class clazz, Class annotation) {
        for (Field field : clazz.getDeclaredFields()) {
            if (null != field.getAnnotation(annotation))
                return field;
        }
        return null;
    }
}
