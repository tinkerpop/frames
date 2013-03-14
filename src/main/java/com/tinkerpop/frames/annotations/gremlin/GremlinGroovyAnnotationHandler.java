package com.tinkerpop.frames.annotations.gremlin;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.ClassUtilities;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedVertexIterable;
import com.tinkerpop.frames.annotations.AnnotationHandler;
import com.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.util.iterators.SingleIterator;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @author Bryn Cooke
 */
public class GremlinGroovyAnnotationHandler implements AnnotationHandler<GremlinGroovy> {
    private final GremlinGroovyScriptEngine engine = new GremlinGroovyScriptEngine();

    @Override
    public Class<GremlinGroovy> getAnnotationType() {
        return GremlinGroovy.class;
    }

    @Override
    public Object processElement(final GremlinGroovy annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph,
                                 final Element element, final Direction direction) {
        if (element instanceof Vertex) {
            return processVertex(annotation, method, arguments, framedGraph, (Vertex) element);
        } else {
            throw new UnsupportedOperationException("This method only works for vertices");
        }
    }

    public Object processVertex(final GremlinGroovy annotation, final Method method, final Object[] arguments, final FramedGraph framedGraph,
                                final Vertex vertex) {
        try {
            final CompiledScript script = this.engine.compile(annotation.value());
            final Bindings bindings = getBindings(method, arguments);
            bindings.put("it", vertex);
            final Object result = script.eval(bindings);
            if (result instanceof Pipe) {
                final Pipe pipe = (Pipe) result;
                pipe.setStarts(new SingleIterator<Element>(vertex));
                final FramedVertexIterable r = new FramedVertexIterable(framedGraph, pipe, ClassUtilities.getGenericClass(method));
                if (ClassUtilities.isGetMethod(method)) {
                    if (ClassUtilities.returnsIterable(method)) {
                        return r;
                    } else {
                        return r.iterator().hasNext() ? r.iterator().next() : null;
                    }
                } else {
                    return result;
                }
            } else {
                return result;
            }
        } catch (ScriptException e) {
            rethrow(e); //Preserve original exception functionality.
            return null;
        }
    }

    private Bindings getBindings(final Method method, final Object[] arguments) {
        Bindings bindings = engine.createBindings();
        Annotation[][] allParameterAnnotations = method.getParameterAnnotations();
        for (int pCount = 0; pCount < allParameterAnnotations.length; pCount++) {
            Annotation parameterAnnotations[] = allParameterAnnotations[pCount];
            for (int aCount = 0; aCount < parameterAnnotations.length; aCount++) {
                Annotation paramAnnotation = parameterAnnotations[aCount];
                if (paramAnnotation instanceof GremlinParam) {
                    bindings.put(((GremlinParam) paramAnnotation).value(), arguments[pCount]);
                    break;
                }
            }
        }
        return bindings;
    }

    public static void rethrow(final Throwable checkedException) {
        GremlinGroovyAnnotationHandler.<RuntimeException>thrownInsteadOf(checkedException);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void thrownInsteadOf(Throwable t) throws T {
        throw (T) t;
    }

}
