package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Edge;
import com.tinkerpop.blueprints.pgm.Element;
import com.tinkerpop.blueprints.pgm.Vertex;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedElement implements InvocationHandler {

    private final FramesManager manager;
    private final Element element;
    private Direction direction;
    private static Method hashCodeMethod;
    private static Method equalsMethod;
    private static Method toStringMethod;


    static {
        try {
            hashCodeMethod = Object.class.getMethod("hashCode", null);
            equalsMethod = Object.class.getMethod("equals", new Class[]{Object.class});
            toStringMethod = Object.class.getMethod("toString", null);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    public FramedElement(final FramesManager manager, final Element element) {
        this.element = element;
        this.manager = manager;
    }

    public FramedElement(final FramesManager manager, final Element element, Direction direction) {
        this(manager, element);
        this.direction = direction;
    }

    public Object invoke(final Object proxy, final Method method, final Object[] arguments) {
        if (method.equals(hashCodeMethod)) {
            return proxyHashCode(proxy);
        } else if (method.equals(equalsMethod)) {
            return proxyEquals(proxy, arguments[0]);
        } else if (method.equals(toStringMethod)) {
            return proxyToString(proxy);
        }

        Annotation[] anns = method.getAnnotations();
        for (Annotation ann : anns) {
            if (ann instanceof Property & isGetter(method)) {
                return element.getProperty(((Property) ann).value());
            } else if (ann instanceof Property & isSetter(method)) {
                element.setProperty(((Property) ann).value(), arguments[0]);
                return null;
            } else if (ann instanceof HalfRelation & isGetter(method)) {
                HalfRelation rel = (HalfRelation) ann;
                return new HalfRelationCollection(this.manager, (Vertex) element, rel.label(), rel.direction(), rel.kind());
            } else if (ann instanceof FullRelation & isGetter(method)) {
                FullRelation rel = (FullRelation) ann;
                return new FullRelationCollection(this.manager, (Vertex) element, rel.label(), rel.direction(), rel.kind());
            } else if (ann instanceof Domain & isGetter(method)) {
                if (this.direction.equals(Direction.STANDARD)) {
                    return this.manager.frame(((Edge) element).getOutVertex(), method.getReturnType());
                } else {
                    return this.manager.frame(((Edge) element).getInVertex(), method.getReturnType());
                }
            } else if (ann instanceof Range & isGetter(method)) {
                if (this.direction.equals(Direction.STANDARD)) {
                    return this.manager.frame(((Edge) element).getInVertex(), method.getReturnType());
                } else {
                    return this.manager.frame(((Edge) element).getOutVertex(), method.getReturnType());
                }
            }
        }
        return null;

    }

    private boolean isGetter(final Method method) {
        return method.getName().startsWith("get");
    }

    private boolean isSetter(final Method method) {
        return method.getName().startsWith("set");
    }


    protected Integer proxyHashCode(final Object proxy) {
        return System.identityHashCode(proxy);
    }

    protected Boolean proxyEquals(final Object proxy, final Object other) {
        return (proxy == other ? Boolean.TRUE : Boolean.FALSE);
    }

    protected String proxyToString(final Object proxy) {
        return proxy.getClass().getName() + '@' + Integer.toHexString(proxy.hashCode());
    }

}
