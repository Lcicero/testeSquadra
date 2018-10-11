package com.br.testesquadra.util;

import java.util.Collection;

import org.apache.commons.collections.Predicate;

/**
 * Utility class used in the selectMultiple method of the CollectionUtil class
 */
@SuppressWarnings("rawtypes")
public class CollectionPredicatePair {
	private final Predicate predicate;
	private final Collection collection;

	/**
	 * Constructs a new CollectionPredicatPair with their predicates and pairs
	 * 
	 * @param predicate
	 *            Condition for evaluation
	 * @param collection
	 *            Collection to be processed
	 */
	public CollectionPredicatePair(final Predicate predicate, final Collection<?> collection) {
		this.predicate = predicate;
		this.collection = collection;
	}

	public Predicate getPredicate() {
		return this.predicate;
	}

	public Collection getCollection() {
		return this.collection;
	}

}