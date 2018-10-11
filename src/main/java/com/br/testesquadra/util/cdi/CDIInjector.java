package com.br.testesquadra.util.cdi;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

@SuppressWarnings("rawtypes")
public class CDIInjector {

	private final InjectionTarget target;
	private final CreationalContext context;

	public CDIInjector(final Class<?> type) {
		this(BeanManagerUtil.getBeanManagerFromJNDI(), type);
	}

	public CDIInjector(final BeanManager beanManager, final Class<?> type) {

		final AnnotatedType<?> annotatedType = beanManager.createAnnotatedType(type);

		this.target = beanManager.createInjectionTarget(annotatedType);
		this.context = beanManager.createCreationalContext(null);

	}

	@SuppressWarnings("unchecked")
	public void inject(final Object component) {
		this.target.inject(component, this.context);
		this.target.postConstruct(component);
	}

	@SuppressWarnings("unchecked")
	public static void performInject(final Object component) {

		final BeanManager beanManager = BeanManagerUtil.getBeanManagerFromJNDI();

		final AnnotatedType<?> annotatedType = beanManager.createAnnotatedType(component.getClass());

		final InjectionTarget target = beanManager.createInjectionTarget(annotatedType);
		final CreationalContext context = beanManager.createCreationalContext(null);

		target.inject(component, context);
		target.postConstruct(component);

	}

}