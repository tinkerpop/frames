package com.tinkerpop.frames.annotations.gremlin;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.ClassUtilities;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedVertexIterable;
import com.tinkerpop.frames.annotations.AnnotationHandler;
import com.tinkerpop.gremlin.groovy.Gremlin;
import com.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.util.iterators.SingleIterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class GremlinGroovyAnnotationHandler implements AnnotationHandler<GremlinGroovy> {
	private final GremlinGroovyScriptEngine engine = new GremlinGroovyScriptEngine();
	private Map<Annotation, CompiledScript> cache = new ConcurrentHashMap<Annotation, CompiledScript>();

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
			if (ClassUtilities.isGetMethod(method)) {

				CompiledScript script = cache.get(annotation);
				if (script == null) {
					script = engine.compile(annotation.value());
					cache.put(annotation, script);
				}

				Bindings bindings = getBindings(method, arguments);
				final Pipe pipe = (Pipe) script.eval(bindings);
				pipe.setStarts(new SingleIterator<Element>(vertex));
				FramedVertexIterable r = new FramedVertexIterable(framedGraph, pipe, ClassUtilities.getGenericClass(method));
				if (ClassUtilities.returnsIterable(method)) {
					return r;
				} else {
					return r.iterator().hasNext() ? r.iterator().next() : null;
				}
			} else {
				throw new UnsupportedOperationException("Gremlin only works with getters");
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
			for(int aCount = 0; aCount < parameterAnnotations.length; aCount++) {
				Annotation paramAnnotation = parameterAnnotations[aCount];
				if(paramAnnotation instanceof Param) {
					bindings.put(((Param) paramAnnotation).value(), arguments[pCount]);
					break;
				}
			}
		}
		return bindings;
	}
	
	public static void rethrow(final Throwable checkedException) {
		GremlinGroovyAnnotationHandler.<RuntimeException> thrownInsteadOf(checkedException);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> void thrownInsteadOf(Throwable t) throws T {
		throw (T) t;
	}

}
