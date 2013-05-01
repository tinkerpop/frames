package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.ClassUtilities;
import com.tinkerpop.frames.FrameEventListener;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.VertexFrame;
import com.tinkerpop.frames.structures.FramedVertexIterable;

import java.lang.reflect.Method;

public class AdjacencyAnnotationHandler implements AnnotationHandler<Adjacency> {

    @Override
    public Class<Adjacency> getAnnotationType() {
        return Adjacency.class;
    }

    @Override
    public Object processElement(final Adjacency annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph,
            final Element element, final Direction direction) {
        if (element instanceof Vertex) {
            return processVertex(annotation, method, arguments, framedGraph, (Vertex) element);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public Object processVertex(final Adjacency adjacency, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Vertex vertex) {
        if (ClassUtilities.isGetMethod(method)) {
            final FramedVertexIterable r = new FramedVertexIterable(framedGraph, vertex.getVertices(adjacency.direction(), adjacency.label()),
                    ClassUtilities.getGenericClass(method));
            if (ClassUtilities.returnsIterable(method)) {
                return r;
            } else {
                return r.iterator().hasNext() ? r.iterator().next() : null;
            }
        } else if (ClassUtilities.isAddMethod(method)) {
            Class<?> returnType = method.getReturnType();
            Vertex newVertex;
            Object returnValue = null;
            if (arguments == null) {
                // Use this method to get the vertex so that the vertex
                // initializer is called.
                returnValue = framedGraph.addVertex(returnType, returnType);
                newVertex = ((VertexFrame) returnValue).asVertex();
            } else {
                newVertex = ((VertexFrame) arguments[0]).asVertex();
            }
            addEdges(adjacency, framedGraph, vertex, newVertex);

            if (returnType.isPrimitive()) {
                return null;
            } else {
                return returnValue;
            }

        } else if (ClassUtilities.isRemoveMethod(method)) {
            removeEdges(adjacency.direction(), adjacency.label(), vertex, ((VertexFrame) arguments[0]).asVertex(), framedGraph);
            return null;
        } else if (ClassUtilities.isSetMethod(method)) {
            removeEdges(adjacency.direction(), adjacency.label(), vertex, null, framedGraph);
            if (ClassUtilities.acceptsIterable(method)) {
                for (Object o : (Iterable) arguments[0]) {
                    Vertex v = ((VertexFrame) o).asVertex();
                    addEdges(adjacency, framedGraph, vertex, v);
                }
                return null;
            } else {
                if (null != arguments[0]) {
                    Vertex newVertex = ((VertexFrame) arguments[0]).asVertex();
                    addEdges(adjacency, framedGraph, vertex, newVertex);
                }
                return null;
            }
        }

        return null;
    }

    private void addEdges(final Adjacency adjacency, final FramedGraph framedGraph, final Vertex vertex, Vertex newVertex) {
        Edge edge = null;
        switch(adjacency.direction()) {
        case OUT:
            callPreCreateIntercept(null, framedGraph, adjacency.label(), vertex, newVertex);
            edge = framedGraph.getBaseGraph().addEdge(null, vertex, newVertex, adjacency.label());
            break;
        case IN:
            callPreCreateIntercept(null, framedGraph, adjacency.label(), newVertex, vertex);
            edge = framedGraph.getBaseGraph().addEdge(null, newVertex, vertex, adjacency.label());
            break;
        case BOTH:
            throw new UnsupportedOperationException("Direction.BOTH it not supported on 'add' or 'set' methods");
        }
        callPostCreateIntercept(null,framedGraph,edge);
    }

    private void removeEdges(final Direction direction, final String label, final Vertex element, final Vertex otherVertex, final FramedGraph framedGraph) {
        final Graph graph = framedGraph.getBaseGraph();
        for (final Edge edge : element.getEdges(direction, label)) {
            if (null == otherVertex || edge.getVertex(direction.opposite()).equals(otherVertex)) {
                callPreDeleteIntercept(framedGraph, edge);
                graph.removeEdge(edge);
            }
        }
    }
    private void callPreCreateIntercept(final Class<?> kind, final FramedGraph<?> framedGraph, final String label, final Vertex outVertex, Vertex inVertex){
        for (FrameEventListener intercept : framedGraph.getFrameEventListeners()){
            intercept.preCreateEdge(kind, framedGraph, label, outVertex, inVertex);
        }
    }

    private void callPreDeleteIntercept(final FramedGraph<?> framedGraph, final Edge edge){
        for (FrameEventListener intercept : framedGraph.getFrameEventListeners()){
            intercept.preDeleteEdge(null,framedGraph,edge);
        }
    }

    private void callPostCreateIntercept(final Class<?> kind, final FramedGraph<?> framedGraph, Edge edge){
        for (FrameEventListener intercept : framedGraph.getFrameEventListeners()){
            intercept.postCreateEdge(kind,framedGraph,edge);
        }
    }
}
