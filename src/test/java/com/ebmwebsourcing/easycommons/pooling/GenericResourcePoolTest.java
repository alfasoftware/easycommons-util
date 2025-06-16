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

import java.lang.Thread.State;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import static org.junit.Assert.*;

import com.ebmwebsourcing.easycommons.thread.SimultaneousTaskExecutor;
import com.ebmwebsourcing.easycommons.thread.TestThread;

/**
 * @author aruffie
 * @author Nicolas Oddoux - EBM WebSourcing
 */
public class GenericResourcePoolTest {

    private class StringResourceHandler implements ResourceHandler<String> {

        public int count;

        public StringResourceHandler() {
            this.count = 0;
        }

        @Override
        public String create() {
            this.count++;
            return new String("test" + this.count);
        }

        @Override
        public void onTake(String resource) {
        }

        @Override
        public void onRelease(String resource) {
        }
    }

    @Test
    public void testGenericResourcePoolInstantiation() {
        new GenericResourcePool<String>(new StringResourceHandler(), 10, 15, PoolPolicy.WAIT);
    }

    @Test
    public void testGenericResourcePoolTake() {
        GenericResourcePool<String> pool = new GenericResourcePool<String>(
                new StringResourceHandler(), 10, 15, PoolPolicy.WAIT);

        String str = pool.take();
        if (str == null) {
            fail("A string must be created and returned by the pool");
        }
    }

    @Test
    public void testGenericResourcePoolRelease() {
        GenericResourcePool<String> pool = new GenericResourcePool<String>(
                new StringResourceHandler(), 10, 15, PoolPolicy.WAIT);

        String str = pool.take();
        if (str == null) {
            fail("A string must be created and returned by the pool");
        }
        pool.release(str);
    }

    @Test(expected = PoolException.class, timeout = 300000)
    public void testGenericResourcePoolMaxReachedSize() {
        GenericResourcePool<String> pool = new GenericResourcePool<String>(
                new StringResourceHandler(), 3, 3, PoolPolicy.WAIT);

        pool.take();
        pool.take();
        pool.take();

        final Thread mainThread = Thread.currentThread();

        Thread interrupter = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mainThread.getState() != State.WAITING) {
                    Thread.yield();
                }

                mainThread.interrupt();
            }
        });
        interrupter.start();

        pool.take();
    }

    @Test(timeout = 30000)
    public void testGenericResourcePoolDynamicPart() throws Exception {
        final GenericResourcePool<String> pool = new GenericResourcePool<String>(
                new StringResourceHandler(), 1, 5, PoolPolicy.WAIT);

        final Semaphore sem = new Semaphore(0);
        
        String str1 = pool.take();
        assertEquals("test1", str1);
        String str2 = pool.take();
        assertEquals("test2", str2);
        final String str3 = pool.take();
        assertEquals("test3", str3);
        String str4 = pool.take();
        assertEquals("test4", str4);
        String str5 = pool.take();
        assertEquals("test5", str5);

        final Thread mainThread = Thread.currentThread();

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                while (mainThread.getState() != State.WAITING) {
                    Thread.yield();
                }

                pool.release(str3);
                try {
                    sem.acquire();
                    String str = pool.take();
                    assertEquals("test5", str);
                } catch (InterruptedException e) {
                    fail(e.getMessage());
                }
            }
        };
        TestThread releaseThread = new TestThread(runnable);
        releaseThread.start();

        String str = pool.take();
        assertEquals("test3", str);
        sem.release();
        
        pool.release(str5);
        
        releaseThread.joinExplosively();
    }

    @Test(timeout = 30000)
    public void testGenericResourcePoolMaxWithMultipleThreads() throws Exception {
        final int poolMaxSize = 50;
        final GenericResourcePool<String> pool = new GenericResourcePool<String>(
                new StringResourceHandler(), 2, poolMaxSize, PoolPolicy.WAIT);

        final AtomicInteger threadWithAResource = new AtomicInteger(0);
        SimultaneousTaskExecutor ste = new SimultaneousTaskExecutor();
        final int threadNb = poolMaxSize;
        for (int i = 0; i < threadNb; i++) {
            ste.registerTask(new Runnable() {

                @Override
                public void run() {
                    pool.take();
                    threadWithAResource.incrementAndGet();
                }
            });
        }
        ste.executeAllRegisteredTasks();        
        
        assertTrue(threadWithAResource.intValue() == poolMaxSize);
    }
    
    @Test(timeout = 30000)
    public void testGenericResourcePoolMaxWithMultipleThreads2() throws Exception {
        final int poolMaxSize = 3;
        final GenericResourcePool<String> pool = new GenericResourcePool<String>(
                new StringResourceHandler(), 2, poolMaxSize, PoolPolicy.WAIT);

        final AtomicInteger threadWithAResource = new AtomicInteger(0);
        SimultaneousTaskExecutor ste = new SimultaneousTaskExecutor();
        final int threadNb = 5;
        for (int i = 0; i < threadNb; i++) {
            ste.registerTask(new Runnable() {

                @Override
                public void run() {
                    String str = pool.take();
                    threadWithAResource.incrementAndGet();
                    assertTrue(threadWithAResource.intValue() <= poolMaxSize);
                    threadWithAResource.decrementAndGet();
                    pool.release(str);
                }
            });
        }
        ste.executeAllRegisteredTasks();        
    }

    @Test(expected = PoolException.class)
    public void testGenericResourcePoolWithRejectPoolPolicy() {
        GenericResourcePool<String> pool = new GenericResourcePool<String>(
                new StringResourceHandler(), 3, 3, PoolPolicy.REJECT);

        pool.take();
        pool.take();
        pool.take();

        pool.take();
    }

    @Test(timeout = 30000)
    public void testConcurrentTakeRelease() throws Exception {
        final GenericResourcePool<String> pool = new GenericResourcePool<String>(
                new StringResourceHandler(), 1, 1, PoolPolicy.WAIT);

        String str = pool.take();
        assertEquals("test1", str);

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    String str = pool.take();
                    assertEquals("test1", str);
                } catch (PoolException e) {
                    fail(e.getMessage());
                }
            }
        };
        TestThread takeThread = new TestThread(runnable);
        takeThread.start();

        while (takeThread.getState() != State.WAITING) {
            Thread.yield();
        }

        pool.release(str);
        
        takeThread.joinExplosively();
    }

    private class StringResourceHandlerWithOnTakeAndOnReleaseImpl implements
            ResourceHandler<String> {

        public boolean onTake;

        public boolean onRelease;

        public StringResourceHandlerWithOnTakeAndOnReleaseImpl() {
            this.onTake = false;
            this.onRelease = false;
        }

        public boolean isOnTake() {
            return this.onTake;
        }

        public boolean isOnRelease() {
            return this.onRelease;
        }

        @Override
        public String create() {
            return new String("test");
        }

        @Override
        public void onTake(String resource) {
            this.onTake = true;
        }

        @Override
        public void onRelease(String resource) {
            this.onRelease = true;
        }
    }

    @Test(timeout = 30000)
    public void testOnTakeAndOnReleaseMethod() throws Exception {
        StringResourceHandlerWithOnTakeAndOnReleaseImpl ressourceHandler = new StringResourceHandlerWithOnTakeAndOnReleaseImpl();
        GenericResourcePool<String> pool = new GenericResourcePool<String>(
                ressourceHandler, 1, 1, PoolPolicy.WAIT);

        assertTrue(!ressourceHandler.isOnTake());
        assertTrue(!ressourceHandler.isOnRelease());
        
        String str = pool.take();
        
        assertTrue(ressourceHandler.isOnTake());
        assertTrue(!ressourceHandler.isOnRelease());
                
        pool.release(str);

        assertTrue(ressourceHandler.isOnTake());
        assertTrue(ressourceHandler.isOnRelease());
    }
}
