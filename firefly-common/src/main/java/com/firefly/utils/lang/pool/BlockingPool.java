package com.firefly.utils.lang.pool;

import java.util.concurrent.TimeUnit;

/**
 * Represents a pool of objects that makes the requesting threads wait if no
 * object is available.
 */
public interface BlockingPool<T> extends Pool<T> {
	/**
	 * Returns an instance of type T from the pool.
	 *
	 * The call is a blocking call, and client threads are made to wait
	 * indefinitely until an object is available. The call implements a fairness
	 * algorithm that ensures that a FCFS service is implemented.
	 *
	 * Clients are advised to react to InterruptedException. If the thread is
	 * interrupted while waiting for an object to become available, the current
	 * implementations sets the interrupted state of the thread to true and
	 * returns null. However this is subject to change from implementation to
	 * implementation.
	 *
	 * @return T an instance of the Object of type T from the pool.
	 */
	T take() throws InterruptedException;

	/**
	 * Returns an instance of type T from the pool, waiting up to the specified
	 * wait time if necessary for an object to become available..
	 *
	 * The call is a blocking call, and client threads are made to wait for time
	 * until an object is available or until the timeout occurs. The call
	 * implements a fairness algorithm that ensures that a FCFS service is
	 * implemented.
	 *
	 * Clients are advised to react to InterruptedException. If the thread is
	 * interrupted while waiting for an object to become available, the current
	 * implementations set the interrupted state of the thread to true and
	 * returns null. However this is subject to change from implementation to
	 * implementation.
	 *
	 *
	 * @param time
	 *            amount of time to wait before giving up, in units of unit
	 * @param unit
	 *            a TimeUnit determining how to interpret the timeout parameter
	 *
	 * @return T an instance of the Object of type T from the pool.
	 *
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 */

	T take(long time, TimeUnit unit) throws InterruptedException;

	/**
	 * Inserts the specified element into this queue, waiting if necessary for
	 * space to become available.
	 *
	 * @param e
	 *            the element to add
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 * @throws ClassCastException
	 *             if the class of the specified element prevents it from being
	 *             added to this queue
	 * @throws NullPointerException
	 *             if the specified element is null
	 * @throws IllegalArgumentException
	 *             if some property of the specified element prevents it from
	 *             being added to this queue
	 */
	void put(T e) throws InterruptedException;

	/**
	 * Inserts the specified element into this queue, waiting up to the
	 * specified wait time if necessary for space to become available.
	 *
	 * @param e
	 *            the element to add
	 * @param timeout
	 *            how long to wait before giving up, in units of {@code unit}
	 * @param unit
	 *            a {@code TimeUnit} determining how to interpret the
	 *            {@code timeout} parameter
	 * @return {@code true} if successful, or {@code false} if the specified
	 *         waiting time elapses before space is available
	 * @throws InterruptedException
	 *             if interrupted while waiting
	 * @throws ClassCastException
	 *             if the class of the specified element prevents it from being
	 *             added to this queue
	 * @throws NullPointerException
	 *             if the specified element is null
	 * @throws IllegalArgumentException
	 *             if some property of the specified element prevents it from
	 *             being added to this queue
	 */
	boolean put(T e, long timeout, TimeUnit unit) throws InterruptedException;

}
