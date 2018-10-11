package com.br.util.testesquadra.model;


/**
 * A CDI Event for data update in cache. Some other methods might cause the real data changed. So the method annotated @{@link MethodInvocationCached} should get the real data
 * again not from cache. Fire this event at the cause position, meanwhile, the implementation of cache should observer it.
 *
 */
public class MethodInvocationCacheUpdateEvent {

	private String className;

	public MethodInvocationCacheUpdateEvent(final String className) {
		super();
		this.className = className;
	}

	public String getClassName() {

		return this.className;
	}

	public void setClassName(final String className) {

		this.className = className;
	}

}