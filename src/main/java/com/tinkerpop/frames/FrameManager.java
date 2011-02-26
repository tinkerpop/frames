package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.frames.util.Relations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FrameManager {

    private Graph graph;

    public FrameManager(final Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return this.graph;
    }

    public <T> T load(final Class<T> clazz, final com.tinkerpop.blueprints.pgm.Vertex vertex) throws Exception {
        final T object = clazz.getConstructor().newInstance();
        for (final Field field : clazz.getDeclaredFields()) {
            final boolean isAccessible = field.isAccessible();
            field.setAccessible(true);
            Annotation annotation;
            if (null != (annotation = field.getAnnotation(Vertex.class))) {
                field.set(object, vertex);
            } else if (null != (annotation = field.getAnnotation(Property.class))) {
                field.set(object, vertex.getProperty(field.getName()));
            } else if (null != (annotation = field.getAnnotation(Relation.class))) {
                Relation relationAnnotation = (Relation) annotation;
                if (relationAnnotation.label().equals(Tokens.DEFAULT))
                    field.set(object, new Relations<T>(this, vertex, field.getName(), relationAnnotation.direction(), relationAnnotation.clazz()));
                else
                    field.set(object, new Relations<T>(this, vertex, relationAnnotation.label(), relationAnnotation.direction(), relationAnnotation.clazz()));
            }
            field.setAccessible(isAccessible);
        }
        return object;
    }

    public <T> T load(final Class<T> clazz, final Object id) throws Exception {
        if (id instanceof com.tinkerpop.blueprints.pgm.Element) {
            return this.load(clazz, id);
        }
        final com.tinkerpop.blueprints.pgm.Vertex vertex = this.graph.getVertex(id);
        return this.load(clazz, vertex);
    }

    public <T> void save(T object) throws Exception {

        Field vertexField = FrameManager.getAnnotatedField(object.getClass(), Vertex.class);
        boolean isAccessible = vertexField.isAccessible();
        vertexField.setAccessible(true);
        com.tinkerpop.blueprints.pgm.Vertex vertex = (com.tinkerpop.blueprints.pgm.Vertex) vertexField.get(object);
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

    public static Field getAnnotatedField(Class clazz, Class annotation) {
        for (Field field : clazz.getDeclaredFields()) {
            if (null != field.getAnnotation(annotation))
                return field;
        }
        return null;
    }
}
