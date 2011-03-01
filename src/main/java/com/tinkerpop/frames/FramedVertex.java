package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.util.AdjacencyCollection;
import com.tinkerpop.frames.util.RelationCollection;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedVertex extends FramedElement {

    private static final String ADD = "add";

    public FramedVertex(final FramesManager manager, final Vertex vertex) {
        super(manager, vertex);
    }

    public Object invoke(final Object proxy, final Method method, final Object[] arguments) {
        final Object returnObject = super.invoke(proxy, method, arguments);
        if (NO_INVOCATION_PATH != returnObject) {
            return returnObject;
        } else {
            final Annotation[] annotations = method.getAnnotations();
            for (final Annotation annotation : annotations) {
                if (annotation instanceof Relation) {
                    final Relation relation = (Relation) annotation;
                    if (isGetMethod(method)) {
                        return new RelationCollection(this.manager, (Vertex) this.element, relation.label(), relation.direction(), getGenericClass(method));
                    } else if (isAddMethod(method)) {
                        if (relation.direction().equals(Direction.STANDARD))
                            this.manager.getGraph().addEdge(null, (Vertex) this.element, ((FramedVertex) Proxy.getInvocationHandler(arguments[0])).getVertex(), relation.label());
                        else
                            this.manager.getGraph().addEdge(null, ((FramedVertex) Proxy.getInvocationHandler(arguments[0])).getVertex(), (Vertex) this.element, relation.label());
                        return null;
                    }
                } else if (annotation instanceof Adjacency) {
                    final Adjacency adjacency = (Adjacency) annotation;
                    if (isGetMethod(method)) {
                        return new AdjacencyCollection(this.manager, (Vertex) this.element, adjacency.label(), adjacency.direction(), getGenericClass(method));
                    } else if (isAddMethod(method)) {
                        if (adjacency.direction().equals(Direction.STANDARD))
                            return this.manager.frame(this.manager.getGraph().addEdge(null, (Vertex) this.element, ((FramedVertex) Proxy.getInvocationHandler(arguments[0])).getVertex(), adjacency.label()), method.getReturnType(), Direction.STANDARD);
                        else
                            return this.manager.frame(this.manager.getGraph().addEdge(null, ((FramedVertex) Proxy.getInvocationHandler(arguments[0])).getVertex(), (Vertex) this.element, adjacency.label()), method.getReturnType(), Direction.INVERSE);
                    }
                }
            }
            throw new RuntimeException("Proxy can not invoke method: " + method);
        }
    }

    private Class getGenericClass(final Method method) {
        final Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedTypeImpl)
            return (Class) ((ParameterizedTypeImpl) returnType).getActualTypeArguments()[0];
        else
            return method.getReturnType();
    }

    protected boolean isAddMethod(final Method method) {
        return method.getName().startsWith(ADD);
    }

    protected Vertex getVertex() {
        return (Vertex) this.element;
    }
}
