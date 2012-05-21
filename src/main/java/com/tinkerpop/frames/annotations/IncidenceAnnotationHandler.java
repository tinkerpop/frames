package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedElement;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.util.ClassUtilities;
import com.tinkerpop.frames.util.IncidenceCollection;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class IncidenceAnnotationHandler implements AnnotationHandler<Incidence> {

    @Override
    public Class<Incidence> getAnnotationType() {
        return Incidence.class;
    }

    @Override
    public Object processVertex(final Incidence incidence, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Vertex element) {
        if (ClassUtilities.isGetMethod(method)) {
            return new IncidenceCollection(framedGraph, element, incidence.label(), incidence.direction(), ClassUtilities.getGenericClass(method));
        } else if (ClassUtilities.isAddMethod(method)) {
            if (incidence.direction().equals(Direction.OUT))
                return framedGraph.addEdge(null, element, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), incidence.label(), Direction.OUT, method.getReturnType());
            else
                return framedGraph.addEdge(null, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), element, incidence.label(), Direction.IN, method.getReturnType());
        } else if (ClassUtilities.isRemoveMethod(method)) {
            framedGraph.getBaseGraph().removeEdge((Edge) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement());
            return null;
        }

        return null;
    }

    @Override
    public Object processEdge(final Incidence annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph, final Edge element, final Direction direction) {
        throw new UnsupportedOperationException();
    }

}
