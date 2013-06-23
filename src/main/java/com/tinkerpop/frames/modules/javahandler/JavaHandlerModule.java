package com.tinkerpop.frames.modules.javahandler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ExecutionException;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedGraphConfiguration;
import com.tinkerpop.frames.Module;

/**
 * <p>
 * Adds support for calling Java methods to handle a frames call. A call to a
 * frames method annotated with {@link JavaHandler} will cause the supplied
 * factory to instantiate an instance of the handler class and call the
 * appropriate method on it.
 * </p>
 * <p>
 * The default factory implementation creates a class who type name is the same
 * as the frame + 'Impl'. For example:
 * </p>
 * <p>
 * Person (frame) => PersonImpl (handler)
 * </p>
 * 
 * @author Bryn Cooke
 */
public class JavaHandlerModule implements Module {

	private JavaHandlerFactory factory = new JavaHandlerFactory() {

		
		private LoadingCache<Class<?>, Class<?>> classCache = CacheBuilder.newBuilder().build(
				new CacheLoader<Class<?>, Class<?>>() {

					@Override
					public Class<?> load(final Class<?> frameClass) throws Exception {
						ProxyFactory proxyFactory = new ProxyFactory(){
							protected ClassLoader getClassLoader() {
								return frameClass.getClassLoader();
							};
						};
						proxyFactory.setUseCache(false);
						String implementationClass = frameClass.getName() + "Impl";
						proxyFactory.setSuperclass(Class.forName(implementationClass));
						Class<?> proxyClass = proxyFactory.createClass();
						return proxyClass;
					}
				});

		@Override
		public <T> T create(final FramedGraph<?> graph, final Element element, final Method method, final Direction direction) {

			try {
				final Class<?> frameClass = method.getDeclaringClass();
				Class<?> returnType = method.getReturnType();
				Class<T> implClass = (Class<T>) classCache.get(frameClass);
				T handler = implClass.newInstance();
				((Proxy) handler).setHandler(new MethodHandler() {
					private DefaultJavaHandlerImpl defaultJavahandlerImpl = new DefaultJavaHandlerImpl(graph, method, element);
					private Object framedElement;

					@Override
					public Object invoke(Object o, Method m, Method proceed, Object[] args) throws Throwable {
						if (!Modifier.isAbstract(m.getModifiers())) {
							return proceed.invoke(o, args);
						} else {
							if(m.getDeclaringClass() == JavaHandlerImpl.class) {
								return m.invoke(defaultJavahandlerImpl, args);
							}
							if (framedElement == null) {
								if (element instanceof Vertex) {
									framedElement = graph.frame((Vertex) element, frameClass);
								} else {
									framedElement = graph.frame((Edge) element, direction, frameClass);
								}
							}
							return m.invoke(framedElement, args);
						}
					}

				});
				return handler;
			} catch (ExecutionException e) {
				throw new JavaHandlerException("Cannot create class for handling framed method", e);
			} catch (InstantiationException e) {
				throw new JavaHandlerException("Problem instantiating Java handler", e);
			} catch (IllegalAccessException e) {
				throw new JavaHandlerException("Problem instantiating Java handler", e);
			}

		}
	};

	/**
	 * Provide an alternative factory for creating objects that handle frames
	 * calls.
	 * 
	 * @param factory The factory to use.
	 * @return The module.
	 */
	public JavaHandlerModule withFactory(JavaHandlerFactory factory) {
		this.factory = factory;
		return this;
	}

	@Override
	public <T extends Graph> T configure(Graph baseGraph, FramedGraphConfiguration config) {

		config.addAnnotationhandler(new JavaAnnotationHandler(factory));
		return (T) baseGraph;
	}

}
