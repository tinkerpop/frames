package com.tinkerpop.frames;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.util.ElementHelper;
import com.tinkerpop.frames.annotations.AnnotationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * The proxy class of a framed element.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedElement implements InvocationHandler {

    private final Direction direction;
    protected final FramedGraph framedGraph;
    protected final Element element;
    private static Method hashCodeMethod;
    private static Method equalsMethod;
    private static Method toStringMethod;
    private static Method asVertexMethod;
    private static Method asEdgeMethod;

    protected static Object NO_INVOCATION_PATH = new Object();

    static {
        try {
            hashCodeMethod = Object.class.getMethod("hashCode");
            equalsMethod = Object.class.getMethod("equals", new Class[]{Object.class});
            toStringMethod = Object.class.getMethod("toString");
            asVertexMethod = VertexFrame.class.getMethod("asVertex");
            asEdgeMethod = EdgeFrame.class.getMethod("asEdge");
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }

    /**
     * @deprecated The direction field will be dropped in the next major release
     */
    public FramedElement(final FramedGraph framedGraph, final Element element, final Direction direction) {
        if (null == framedGraph) {
            throw new IllegalArgumentException("FramedGraph can not be null");
        }

        if (null == element) {
            throw new IllegalArgumentException("Element can not be null");
        }

        this.element = element;
        this.framedGraph = framedGraph;
        this.direction = direction;
    }

    public FramedElement(final FramedGraph framedGraph, final Element element) {
        this(framedGraph, element, Direction.OUT);
    }

    public Object invoke(final Object proxy, final Method method, final Object[] arguments) {

        if (method.equals(hashCodeMethod)) {
            return this.element.hashCode();
        } else if (method.equals(equalsMethod)) {
            return this.proxyEquals(arguments[0]);
        } else if (method.equals(toStringMethod)) {
            return this.element.toString();
        } else if (method.equals(asVertexMethod) || method.equals(asEdgeMethod)) {
            return this.element;
        }

        final Annotation[] annotations = method.getAnnotations();
        Map<Class<? extends Annotation>, AnnotationHandler<?>> annotationHandlers = this.framedGraph.getConfig().getAnnotationHandlers();
        for (final Annotation annotation : annotations) {
			AnnotationHandler annotationHandler = annotationHandlers.get(annotation.annotationType());
            if (annotationHandler != null) {
                return annotationHandler.processElement(annotation, method, arguments, this.framedGraph, this.element, this.direction);
            }
        }

        return NO_INVOCATION_PATH;
    }

    private Boolean proxyEquals(final Object other) {
        if (other instanceof VertexFrame) {
            return this.element.equals(((VertexFrame) other).asVertex());
        } if (other instanceof EdgeFrame) {
            return this.element.equals(((EdgeFrame) other).asEdge());
        } else if (other instanceof Element) {
            return ElementHelper.areEqual(this.element, other);
        } else {
            return Boolean.FALSE;
        }
    }

    public Element getElement() {
        return this.element;
    }
}
