package com.tinkerpop.frames.domain.incidences;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerImpl;

public abstract class KnowsImpl implements Knows, JavaHandlerImpl<Edge> {

	@Override
	@JavaHandler
	public String getNames() {
		return getDomain().getName() + "<->" + getRange().getName();
	}
}
