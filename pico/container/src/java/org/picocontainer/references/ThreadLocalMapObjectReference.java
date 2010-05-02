/*****************************************************************************
 * Copyright (C) 2003-2010 PicoContainer Committers. All rights reserved.    *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.picocontainer.references;

import org.picocontainer.ObjectReference;

import java.util.Map;

/**
 * Gets and sets references on a map stored in Thread Local  
 * @author Paul Hammant
 */
public class ThreadLocalMapObjectReference<T> implements ObjectReference<T> {
    private final ThreadLocal<Map<Object, T>> threadLocal;
    private final Object key;

    public ThreadLocalMapObjectReference(ThreadLocal<Map<Object, T>> threadLocal, Object key) {
        this.threadLocal = threadLocal;
        this.key = key;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return threadLocal.get().get(key) ;
    }

    @SuppressWarnings("unchecked")
    public void set(T item) {
        threadLocal.get().put(key, item) ;

    }
}
