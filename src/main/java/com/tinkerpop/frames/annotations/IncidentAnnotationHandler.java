package com.tinkerpop.frames.annotations;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Incident;
import com.tinkerpop.frames.FramedElement;
import com.tinkerpop.frames.FramesManager;
import com.tinkerpop.frames.util.IncidentCollection;
import com.tinkerpop.frames.util.ClassUtilities;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class IncidentAnnotationHandler implements AnnotationHandler<Incident> {

    @Override
    public Class<Incident> getAnnotationType() {
        return Incident.class;
    }

    @Override
    public Object processVertex(Incident incident, Method method, Object[] arguments, FramesManager manager, Vertex element) {
        if (ClassUtilities.isGetMethod(method)) {
            return new IncidentCollection(manager, element, incident.label(), incident.direction(), ClassUtilities.getGenericClass(method));
        } else if (ClassUtilities.isAddMethod(method)) {
            if (incident.direction().equals(Direction.OUT))
                return manager.frame(manager.getGraph().addEdge(null, element, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), incident.label()), Direction.OUT, method.getReturnType());
            else
                return manager.frame(manager.getGraph().addEdge(null, (Vertex) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement(), element, incident.label()), Direction.IN, method.getReturnType());
        } else if (ClassUtilities.isRemoveMethod(method)) {
            manager.getGraph().removeEdge((Edge) ((FramedElement) Proxy.getInvocationHandler(arguments[0])).getElement());
            return null;
        }

        return null;
    }

    @Override
    public Object processEdge(Incident annotation, Method method, Object[] arguments, FramesManager manager, Edge element, Direction direction) {
        throw new UnsupportedOperationException();
    }

}
