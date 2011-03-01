package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Edge;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedEdge extends FramedElement {

    private Relation.Direction direction;

    public FramedEdge(final FramesManager manager, final Edge edge, final Relation.Direction direction) {
        super(manager, edge);
        this.direction = direction;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] arguments) {
        final Object returnObject = super.invoke(proxy, method, arguments);
        if (NO_INVOCATION_PATH != returnObject) {
            return returnObject;
        } else {
            final Annotation[] anns = method.getAnnotations();
            for (final Annotation ann : anns) {
                if (ann instanceof Domain & isGetMethod(method)) {
                    if (this.direction.equals(Relation.Direction.STANDARD)) {
                        return this.manager.frame(((Edge) element).getOutVertex(), method.getReturnType());
                    } else {
                        return this.manager.frame(((Edge) element).getInVertex(), method.getReturnType());
                    }
                } else if (ann instanceof Range & isGetMethod(method)) {
                    if (this.direction.equals(Relation.Direction.STANDARD)) {
                        return this.manager.frame(((Edge) element).getInVertex(), method.getReturnType());
                    } else {
                        return this.manager.frame(((Edge) element).getOutVertex(), method.getReturnType());
                    }
                }
            }
            throw new RuntimeException("Proxy can not invoke method: " + method);
        }
    }

    protected Edge getEdge() {
        return (Edge) this.element;
    }
}
