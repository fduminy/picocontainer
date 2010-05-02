/*****************************************************************************
 * Copyright (C) 2003-2010 PicoContainer Committers. All rights reserved.    *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/

package org.picocontainer.behaviors;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.ObjectReference;
import org.picocontainer.Parameter;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.Characteristics;
import org.picocontainer.ComponentMonitor;
import org.picocontainer.references.SimpleReference;
import org.picocontainer.LifecycleStrategy;

import java.util.Properties;

/**
 * factory class creating cached behaviours
 * @author Aslak Helles&oslash;y
 * @author <a href="Rafal.Krzewski">rafal@caltha.pl</a>
 * @author Konstantin Pribluda
 */
@SuppressWarnings("serial")
public class Caching extends AbstractBehavior {

    public <T> ComponentAdapter<T> createComponentAdapter(
			ComponentMonitor componentMonitor,
			LifecycleStrategy lifecycleStrategy,
			Properties componentProperties, Object key,
			Class<T> componentImplementation, Parameter... parameters)
			throws PicoCompositionException {
		if (removePropertiesIfPresent(componentProperties,
				Characteristics.NO_CACHE)) {
			return super.createComponentAdapter(componentMonitor,
					lifecycleStrategy, componentProperties, key,
					componentImplementation, parameters);
		}
		removePropertiesIfPresent(componentProperties, Characteristics.CACHE);
        return componentMonitor.newBehavior(new Cached<T>(super.createComponentAdapter(componentMonitor,
				lifecycleStrategy, componentProperties, key,
				componentImplementation, parameters),
                new SimpleReference<Storing.Stored.Instance<T>>()));

	}

	public <T> ComponentAdapter<T> addComponentAdapter(
			ComponentMonitor componentMonitor,
			LifecycleStrategy lifecycleStrategy,
			Properties componentProperties, ComponentAdapter<T> adapter) {
		if (removePropertiesIfPresent(componentProperties,
				Characteristics.NO_CACHE)) {
			return super.addComponentAdapter(componentMonitor,
					lifecycleStrategy, componentProperties, adapter);
		}
		removePropertiesIfPresent(componentProperties, Characteristics.CACHE);
        ComponentAdapter<T> delegate = super.addComponentAdapter(componentMonitor, lifecycleStrategy, componentProperties, adapter);
        return componentMonitor.newBehavior(componentMonitor.newBehavior(new Cached<T>(delegate, new SimpleReference<Storing.Stored.Instance<T>>())));
	}

    /**
     * <p>
     * {@link org.picocontainer.ComponentAdapter} implementation that caches the component instance.
     * </p>
     * <p>
     * This adapter supports components with a lifecycle, as it is a
     * {@link org.picocontainer.ChangedBehavior lifecycle manager} which will apply the delegate's
     * {@link org.picocontainer.LifecycleStrategy lifecycle strategy} to the cached
     * component instance. The lifecycle state is maintained so that the component
     * instance behaves in the expected way: it can't be started if already started,
     * it can't be started or stopped if disposed, it can't be stopped if not
     * started, it can't be disposed if already disposed.
     * </p>
     *
     * @author Mauro Talevi
     */
    @SuppressWarnings("serial")
    public static class Cached<T> extends Storing.Stored<T> {

        public Cached(ComponentAdapter<T> delegate) {
            this(delegate, new SimpleReference<Instance<T>>());
        }

        public Cached(ComponentAdapter<T> delegate, ObjectReference<Instance<T>> instanceReference) {
            super(delegate, instanceReference);
        }

        public String getDescriptor() {
            return "Cached" + getLifecycleDescriptor();
        }
    }
}
