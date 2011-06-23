package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.util.AdjacencyCollection;
import com.tinkerpop.frames.util.RelationCollection;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * The proxy class of a framed vertex.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class FramedVertex extends FramedElement {

    private static final String ADD = "add";

    public FramedVertex(final FramesManager manager, final Vertex vertex) {
        super(manager, vertex);
    }

    private boolean returnsCollection(final Method method) {
        return Collection.class.isAssignableFrom(method.getReturnType());
    }

    private boolean acceptsCollection(final Method method) {

        return 1 == method.getParameterTypes().length
                && Collection.class.isAssignableFrom(method.getParameterTypes()[0]);
    }

    public Object invoke(final Object proxy, final Method method, final Object[] arguments) {
        final Object returnObject = super.invoke(proxy, method, arguments);
        if (NO_INVOCATION_PATH != returnObject) {
            return returnObject;
        } else {
            if (isVertexGetter(method)) {
                return getVertex();
            }

            final Annotation[] annotations = method.getAnnotations();
            for (final Annotation annotation : annotations) {
                if (annotation instanceof Relation) {
                    final Relation relation = (Relation) annotation;
                    if (isGetMethod(method)) {
                        RelationCollection r = new RelationCollection(this.manager, (Vertex) this.element, relation.label(), relation.direction(), getGenericClass(method));
                        if (returnsCollection(method)) {
                            return r;
                        } else {
                            return r.iterator().hasNext() ? r.iterator().next() : null;
                        }
                    } else if (isAddMethod(method)) {
                        if (relation.direction().equals(Direction.STANDARD))
                            this.manager.getGraph().addEdge(null, (Vertex) this.element, ((FramedVertex) Proxy.getInvocationHandler(arguments[0])).getVertex(), relation.label());
                        else
                            this.manager.getGraph().addEdge(null, ((FramedVertex) Proxy.getInvocationHandler(arguments[0])).getVertex(), (Vertex) this.element, relation.label());
                        return null;
                    } else if (isRemoveMethod(method)) {
                        this.removeEdges(relation.direction(), relation.label(), ((FramedVertex) Proxy.getInvocationHandler(arguments[0])).getVertex());
                        return null;
                    } else if (isSetMethod(method)) {
                        this.removeEdges(relation.direction(), relation.label(), null);
                        if (acceptsCollection(method)) {
                            for (Object o : (Collection) arguments[0]) {
                                Vertex v = ((FramedVertex) Proxy.getInvocationHandler(o)).getVertex();
                                if (relation.direction().equals(Direction.STANDARD))
                                    this.manager.getGraph().addEdge(null, (Vertex) this.element, v, relation.label());
                                else
                                    this.manager.getGraph().addEdge(null, v, (Vertex) this.element, relation.label());
                            }
                            return null;
                        } else {
                            if (relation.direction().equals(Direction.STANDARD))
                                this.manager.getGraph().addEdge(null, (Vertex) this.element, ((FramedVertex) Proxy.getInvocationHandler(arguments[0])).getVertex(), relation.label());
                            else
                                this.manager.getGraph().addEdge(null, ((FramedVertex) Proxy.getInvocationHandler(arguments[0])).getVertex(), (Vertex) this.element, relation.label());
                            return null;
                        }
                    }
                } else if (annotation instanceof Adjacency) {
                    final Adjacency adjacency = (Adjacency) annotation;
                    if (isGetMethod(method)) {
                        return new AdjacencyCollection(this.manager, (Vertex) this.element, adjacency.label(), adjacency.direction(), getGenericClass(method));
                    } else if (isAddMethod(method)) {
                        if (adjacency.direction().equals(Direction.STANDARD))
                            return this.manager.frame(this.manager.getGraph().addEdge(null, (Vertex) this.element, ((FramedVertex) Proxy.getInvocationHandler(arguments[0])).getVertex(), adjacency.label()), Direction.STANDARD, method.getReturnType());
                        else
                            return this.manager.frame(this.manager.getGraph().addEdge(null, ((FramedVertex) Proxy.getInvocationHandler(arguments[0])).getVertex(), (Vertex) this.element, adjacency.label()), Direction.INVERSE, method.getReturnType());
                    } else if (isRemoveMethod(method)) {
                        this.manager.getGraph().removeEdge(((FramedEdge) Proxy.getInvocationHandler(arguments[0])).getEdge());
                        return null;
                    }
                } /*else if (annotation instanceof GremlinInference) {
                    final GremlinInference gremlinInference = (GremlinInference) annotation;
                    if (isGetMethod(method)) {
                        Pipe<Vertex, Vertex> pipe = Gremlin.compile(gremlinInference.script());
                        pipe.setStarts(new SingleIterator<Vertex>(this.getVertex()));
                        return new IterableCollection(new FramingVertexIterable(this.manager, pipe, getGenericClass(method)));
                    }
                }*/
            }
            throw new RuntimeException("Proxy can not invoke method: " + method);
        }
    }

    protected boolean isVertexGetter(final Method method) {
        return method.getName().equals("asVertex");
    }

    private Class getGenericClass(final Method method) {
        final Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedTypeImpl)
            return (Class) ((ParameterizedTypeImpl) returnType).getActualTypeArguments()[0];
        else
            return method.getReturnType();
    }

    private void removeEdges(final Direction direction, final String label, final Vertex otherVertex) {
        final Graph graph = this.manager.getGraph();
        List<Edge> toRemove = new LinkedList<Edge>();
        if (direction.equals(Direction.STANDARD)) {
            for (final Edge edge : this.getVertex().getOutEdges(label)) {
                if (null == otherVertex || edge.getInVertex().equals(otherVertex)) {
                    toRemove.add(edge);
                }
            }
        } else {
            for (final Edge edge : this.getVertex().getInEdges(label)) {
                if (null == otherVertex || edge.getOutVertex().equals(otherVertex)) {
                    toRemove.add(edge);
                }
            }
        }
        for (final Edge edge : toRemove) {
            graph.removeEdge(edge);
        }
    }

    protected boolean isAddMethod(final Method method) {
        return method.getName().startsWith(ADD);
    }

    protected Vertex getVertex() {
        return (Vertex) this.element;
    }
}
