package com.firefly.utils.lang.pool;

import com.firefly.utils.concurrent.Promise;

/**
 * @author Pengtao Qiu
 */
public interface CompletableObjectFactory<T> {

    Promise.Completable<T> createNew();

}
