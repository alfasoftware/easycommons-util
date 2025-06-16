/**
 * Copyright (c) 2010-2012 EBM WebSourcing, 2012-2023 Linagora
 * 
 * This program/library is free software: you can redistribute it and/or modify
 * it under the terms of the New BSD License (3-clause license).
 *
 * This program/library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the New BSD License (3-clause license)
 * for more details.
 *
 * You should have received a copy of the New BSD License (3-clause license)
 * along with this program/library; If not, see http://directory.fsf.org/wiki/License:BSD_3Clause/
 * for the New BSD License (3-clause license).
 */
package com.ebmwebsourcing.easycommons.pooling;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * A pool of resources.
 * The &lt;T&gt; resource life is managed thank to its {@link ResourceHandler}.
 * A minimum number of resources are created at the initialization of the pool.
 * The maximum number of resources corresponds to the limit of resources existing at the
 * same time.
 * Two policy are available for the pool:
 *  - WAIT: if a resource is taking from the pool while there is no one anymore, the thread
 * waits an available resource (releasing by another thread)
 *  - REJECT: if a resource is taking from the pool while there is no one anymore, an exception
 * is thrown.
 * 
 * @author aruffie
 * @author Nicolas Oddoux - EBM WebSourcing
 */
public class GenericResourcePool<T> {

    // the list of used resources
    private List<T> usedResources;

    // the list of available resources
    private List<T> availableResources;
    
    // the semaphore to count the number of available resources
    private Semaphore semaphore;

    // the resource handler
    private ResourceHandler<T> resourceHandler;
    
    // the pool policy
    private PoolPolicy poolPolicy;

    /**
     * Instantiate a new resource pool with specified resource handler, minimum size
     * and maximum size of the pool and pool policy
     * 
     * @param ressourceHandler
     *            A {@link ResourceHandler} in order to manage resources of current pool. it
     *            cannot be null.
     * @param minSize
     *            the minimum number of resources in the current pool (created at the
     *            initialization).
     * @param maxSize
     *            the maximum number of resources in the current pool (limit of the
     *            pool). It must be greater or equals to the specified minSize.
     *            The maximum value is Integer.MAX_VALUE
     * @param poolPolicy
     *            the {@link PoolPolicy} to adopt when the maximum size is reached. it
     *            cannot be null.
     * 
     */
    public GenericResourcePool(final ResourceHandler<T> ressourceHandler, final int minSize,
            final int maxSize, final PoolPolicy poolPolicy) {

        assert ressourceHandler != null;
        assert minSize >= 0;
        assert maxSize >= minSize;
        assert poolPolicy != null;
        
        this.resourceHandler = ressourceHandler;
        this.poolPolicy = poolPolicy;
        
        this.semaphore = new Semaphore(maxSize);
        this.availableResources = new ArrayList<T>();
        this.usedResources = new ArrayList<T>();

        /*
         * Instantiate the minimum number of resources
         */
        for (int i = 0; i < minSize; i++) {
            this.availableResources.add(this.resourceHandler.create());
        }
    }

    /**
     * <p>Take one unused resource in the current pool. After getting a resource from the pool
     * and before returning resource, the method onTake() of the resource handler
     * is called.</p>
     * <p>
     * WARNING: The following pattern must be use to avoid not to release some resources
     * </p>
     * <pre>
     * String st = null;
     * try {
     *     st = stringPool.take();
     *     // ...
     * } catch (PoolException e) { // Optional catch clause to treat the pool exception
     *     // ...
     * } finally { // Mandatory finally clause to release the resource in any cases
     *     if(st != null) {
     *         stringPool.release(st);
     *     }
     * }
     * </pre>
     * 
     * @return one &lt;T&gt;
     * 
     * @throws PoolException
     *             if the current thread is interrupted for the pool policy WAIT
     *             or if there is no more available resource in the pool for the
     *             pool policy REJECT
     * 
     */
    public final T take() {
        if(this.poolPolicy == PoolPolicy.WAIT) {
            try {
                this.semaphore.acquire();
            } catch (InterruptedException e) {
                throw new PoolException(e);
            }
        } else {
            boolean available = this.semaphore.tryAcquire();
            if(!available) {
                throw new PoolException("There is no more available resource in the pool.");
            }
        }
        T resource = getAvailableResource();
        return resource;
    }
    
    private synchronized T getAvailableResource() {
        T resource;
        
        if(this.availableResources.isEmpty()) {
            resource = this.resourceHandler.create();
        } else {
            resource = this.availableResources.remove(0);
        }
        this.usedResources.add(resource);
        this.resourceHandler.onTake(resource);
        
        return resource;
    }

    /**
     * Release the specified resource 
     * After putting back a resource in the pool, the method onRelease() of the resource
     * handler is called.
     * 
     * @param resource
     *            The resource to release
     */
    public final void release(final T resource) {
        this.putBackResource(resource);
        this.semaphore.release(); 
    }
    
    private synchronized void putBackResource(T resource) {
        assert this.usedResources.remove(resource);
        this.availableResources.add(resource);
        this.resourceHandler.onRelease(resource);
    }
}
