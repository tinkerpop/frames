package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Edge;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * The proxy class of a framed edge.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Joshua Shinavier (http://fortytwo.net)
 */
public class FramedEdge extends FramedElement {

    private Direction direction;

    public FramedEdge(final FramesManager manager, final Edge edge, final Direction direction) {
        super(manager, edge);
        this.direction = direction;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] arguments) {
        final Object returnObject = super.invoke(proxy, method, arguments);
        if (NO_INVOCATION_PATH != returnObject) {
            return returnObject;
        } else {
            if (isEdgeGetter(method)) {
                return getElement();
            }

            final Annotation[] anns = method.getAnnotations();
            for (final Annotation ann : anns) {
                if (ann instanceof Domain & isGetMethod(method)) {
                    if (this.direction.equals(Direction.STANDARD)) {
                        return this.manager.frame(((Edge) element).getOutVertex(), method.getReturnType());
                    } else {
                        return this.manager.frame(((Edge) element).getInVertex(), method.getReturnType());
                    }
                } else if (ann instanceof Range & isGetMethod(method)) {
                    if (this.direction.equals(Direction.STANDARD)) {
                        return this.manager.frame(((Edge) element).getInVertex(), method.getReturnType());
                    } else {
                        return this.manager.frame(((Edge) element).getOutVertex(), method.getReturnType());
                    }
                }
            }
            throw new RuntimeException("Proxy can not invoke method: " + method);
        }
    }

    protected boolean isEdgeGetter(final Method method) {
        return method.getName().equals("asEdge");
    }

    protected Edge getEdge() {
        return (Edge) this.element;
    }
}
