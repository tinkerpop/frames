package com.tinkerpop.frames;

import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.frames.util.FullRelationCollection;
import com.tinkerpop.frames.util.HalfRelationCollection;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FramedVertex extends FramedElement {

    public FramedVertex(final FramesManager manager, final Vertex vertex) {
        super(manager, vertex);
    }

    public Object invoke(final Object proxy, final Method method, final Object[] arguments) {
        final Object returnObject = super.invoke(proxy, method, arguments);
        if (NO_INVOCATION_PATH != returnObject) {
            return returnObject;
        } else {
            final Annotation[] anns = method.getAnnotations();
            for (final Annotation ann : anns) {
                if (ann instanceof Relation & isGetter(method)) {
                    Relation rel = (Relation) ann;
                    Class cast;
                    Type returnType = method.getGenericReturnType();
                    if (returnType instanceof ParameterizedTypeImpl)
                        cast = (Class) ((ParameterizedTypeImpl) returnType).getActualTypeArguments()[0];
                    else
                        cast = method.getReturnType();

                    if (rel.type().equals(Relation.Type.HALF))
                        return new HalfRelationCollection(this.manager, (Vertex) element, rel.label(), rel.direction(), cast);
                    else
                        return new FullRelationCollection(this.manager, (Vertex) element, rel.label(), rel.direction(), cast);
                }
            }
            throw new RuntimeException("Proxy can not invoke method: " + method);
        }
    }
}
