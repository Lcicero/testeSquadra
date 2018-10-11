package com.br.testesquadra.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.collections.CollectionUtils;

/**
 * Utility class that provides utility methods for collections.
 * 
 */
public class CollectionUtil extends CollectionUtils {

	/** Default construct */
	protected CollectionUtil() {
		// Utility class
	}

	/**
	 * This method is used to filter multiple collections based on an array of <code>CollectionPredicatePair</code>'s Each object will be evaluated by a predicate and if the
	 * evaluation passes it is added to the associated collection
	 * 
	 * @param mainCollection
	 *            Collection to be processed
	 * @param pairs
	 *            Collection pairs
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void selectMultiple(final Collection mainCollection, final CollectionPredicatePair... pairs) {

		for (final Object object : mainCollection) {

			for (final CollectionPredicatePair pair : pairs) {
				if (pair.getPredicate().evaluate(object)) {
					pair.getCollection().add(object);
				}
			}

		}

	}

	/**
	 * Get the first object in the collection
	 * 
	 * @param collection
	 *            Collection to be processed
	 * 
	 * @return First object in the collection
	 */
	public static <T> T getFirst(final Collection<T> collection) {

		if (isNotEmpty(collection)) {
			return collection.iterator().next();
		}

		return null;

	}

	/**
	 * Get the first object in the list
	 * 
	 * @param collection
	 *            List to be processed
	 * 
	 * @return First object in the list
	 */
	public static <T> T getFirst(final List<T> list) {

		if (isNotEmpty(list)) {
			return list.get(0);
		}

		return null;

	}

	/**
	 * Get the last object in the list
	 * 
	 * @param <T>
	 *            Object to be returned
	 * @param collection
	 *            Collection to be processed
	 * @return Object
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getLast(final Collection<T> collection) {

		if (isNotEmpty(collection)) {
			return (T) get(collection, collection.size() - 1);
		}

		return null;

	}

	/**
	 * Get the last object in the list
	 * 
	 * @param <T>
	 *            Object to be returned
	 * @param list
	 *            List to be processed
	 * @return Object
	 */
	public static <T> T getLast(final List<T> list) {

		if (isNotEmpty(list)) {
			return list.get(list.size() - 1);
		}

		return null;

	}

	/**
	 * Checks if a list has a next record or a previous record.
	 * 
	 * @param iterator
	 *            ListIterator to be processed
	 * @param previous
	 *            If true will check for previous record, if false will check for next record
	 * 
	 * @return Boolean
	 */
	public static <T> boolean hasPreviousOrNext(final ListIterator<T> iterator, final boolean previous) {

		return previous && iterator.hasPrevious() || !previous && iterator.hasNext();

	}

	/**
	 * Get the next record or previous record.
	 * 
	 * @param <T>
	 *            Type of object in List
	 * @param iterator
	 *            ListIterator to be processed
	 * @param previous
	 *            If true will return the previous record, if false will return the next record
	 * 
	 * @return Object
	 */
	public static <T> T previousOrNext(final ListIterator<T> iterator, final boolean previous) {

		if (previous) {
			return iterator.previous();
		}

		return iterator.next();

	}

	/**
	 * Clears the specified collection and add a single item
	 * 
	 * @param collection
	 *            Collection to be cleared
	 * @param singleItem
	 *            Item to be added
	 */
	public static <T> void setSingle(final Collection<T> collection, final T singleItem) {

		collection.clear();

		if (singleItem != null) {
			collection.add(singleItem);
		}

	}

	/**
	 * Create a list of a single object
	 * 
	 * @param <T>
	 *            Type from list to be returned
	 * @param object
	 *            Object to be added to the list
	 * 
	 * @return List
	 */
	public static <T> List<T> singletonArrayList(final T object) {

		final List<T> list = new ArrayList<>();
		list.add(object);

		return list;

	}

}
