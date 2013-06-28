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
import com.tinkerpop.frames.modules.Module;

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

	// We don't want to use the global class cache. Instead we cache the classes
	// at the module level.
	private LoadingCache<Class<?>, Class<?>> classCache = CacheBuilder
			.newBuilder().build(new CacheLoader<Class<?>, Class<?>>() {

				@Override
				public Class<?> load(final Class<?> handlerClass)
						throws Exception {
					ProxyFactory proxyFactory = new ProxyFactory() {
						protected ClassLoader getClassLoader() {
							return handlerClass.getClassLoader();
						};
					};
					proxyFactory.setUseCache(false);
					proxyFactory.setSuperclass(handlerClass);
					Class<?> proxyClass = proxyFactory.createClass();
					return proxyClass;
				}
			});

	private JavaHandlerFactory factory = new JavaHandlerFactory() {

		@Override
		public <T> T create(Class<T> handlerClass)
				throws InstantiationException, IllegalAccessException {
			return handlerClass.newInstance();
		}

	};

	/**
	 * Provide an alternative factory for creating objects that handle frames
	 * calls.
	 * 
	 * @param factory
	 *            The factory to use.
	 * @return The module.
	 */
	public JavaHandlerModule withFactory(JavaHandlerFactory factory) {
		this.factory = factory;
		return this;
	}

	@Override
	public Graph configure(Graph baseGraph, FramedGraphConfiguration config) {

		config.addAnnotationhandler(new JavaAnnotationHandler(factory, classCache));
		return baseGraph;
	}

}
