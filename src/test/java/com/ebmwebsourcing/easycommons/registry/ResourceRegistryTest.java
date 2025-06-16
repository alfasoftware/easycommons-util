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
package com.ebmwebsourcing.easycommons.registry;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.ebmwebsourcing.easycommons.registry.concurrency.ConcurrentRegisterTestTask;
import com.ebmwebsourcing.easycommons.registry.concurrency.ConcurrentUnregisterTestTask;
import com.ebmwebsourcing.easycommons.thread.SimultaneousTaskExecutor;

import static org.junit.Assert.*;

/**
 * @author ofabre
 * 
 */
public class ResourceRegistryTest {

    private static final String KEY_TO_BIND = "KEY_TO_BIND";

    private static final int loopCount = 100;

    private static final int simultaneousThreads = 100;

    @Test
    public void testRegisterAndLookup() {
        ResourceRegistry<Object> resourceRegistry = new ResourceRegistry<Object>();

        Object objectToBind = new Object();

        resourceRegistry.register(KEY_TO_BIND, objectToBind);

        assertEquals(objectToBind, resourceRegistry.lookup(KEY_TO_BIND));
    }

    @Test
    public void testRegisterAndUnregister() {
        ResourceRegistry<Object> resourceRegistry = new ResourceRegistry<Object>();

        Object objectToBind = new Object();

        resourceRegistry.register(KEY_TO_BIND, objectToBind);

        assertEquals(objectToBind, resourceRegistry.lookup(KEY_TO_BIND));

        resourceRegistry.unregister(KEY_TO_BIND);

        assertNull(resourceRegistry.lookup(KEY_TO_BIND));
    }

    @Test
    public void testRegisterTwiceWithTheSameKey() {
        ResourceRegistry<Object> resourceRegistry = new ResourceRegistry<Object>();

        Object objectToBind = new Object();

        resourceRegistry.register(KEY_TO_BIND, objectToBind);
        try {
            resourceRegistry.register(KEY_TO_BIND, objectToBind);
            fail("Must throw a KeyAlreadyBoundException if the register method "
                    + "is called twice on the same registry instance with the same key as parameter");
        } catch (KeyAlreadyBoundException e) {
            // Its the expected exception
        }

    }

    @Test
    public void testUnregisterWithNotRegisteredKey() {
        ResourceRegistry<Object> resourceRegistry = new ResourceRegistry<Object>();

        try {
            resourceRegistry.unregister(KEY_TO_BIND);
            fail("Must throw a KeyNotFoundException if the unregister method "
                    + "is called with a non previously registered key");
        } catch (KeyNotFoundException e) {
            // Its the expected exception
        }

    }

    @Test
    public void testConcurrentRegisteredWithSameKey() throws InterruptedException {
        for (int i = 0; i < loopCount; i++) {
            ResourceRegistry<Object> resourceRegistry = new ResourceRegistry<Object>();
            
            SimultaneousTaskExecutor simultaneousTaskExecutor = new SimultaneousTaskExecutor();
            AtomicInteger counter = new AtomicInteger(0);
            for (int j = 0; j < simultaneousThreads; j++) {
                simultaneousTaskExecutor.registerTask(new ConcurrentRegisterTestTask(counter, resourceRegistry, KEY_TO_BIND));
            }
            simultaneousTaskExecutor.executeAllRegisteredTasks();
            assertEquals("Failed during loop: " + i, simultaneousThreads - 2, counter.longValue());
        }
    }
    
    @Test
    public void testConcurrentUnregisteredWithSameKey() throws InterruptedException {
        for (int i = 0; i < loopCount; i++) {
            ResourceRegistry<Object> resourceRegistry = new ResourceRegistry<Object>();
            resourceRegistry.register(KEY_TO_BIND, new Object());
            
            SimultaneousTaskExecutor simultaneousTaskExecutor = new SimultaneousTaskExecutor();
            AtomicInteger counter = new AtomicInteger(0);
            for (int j = 0; j < simultaneousThreads; j++) {
                simultaneousTaskExecutor.registerTask(new ConcurrentUnregisterTestTask(counter, resourceRegistry, KEY_TO_BIND));
            }
            simultaneousTaskExecutor.executeAllRegisteredTasks();
            assertEquals("Failed during loop: " + i, simultaneousThreads - 2, counter.longValue());
        }
    }

}
