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
package com.ebmwebsourcing.easycommons.xml;

import javax.xml.stream.XMLInputFactory;

import com.ebmwebsourcing.easycommons.pooling.GenericResourcePool;
import com.ebmwebsourcing.easycommons.pooling.PoolException;
import com.ebmwebsourcing.easycommons.pooling.PoolPolicy;
import com.ebmwebsourcing.easycommons.pooling.ResourceHandler;

/**
 * This class represent a {@link XMLInputFactory} pool, which provided
 * {@link XMLInputFactory} object as resource.
 * 
 * @author Christophe DENEUX - EBMWebSourcing
 */
public class XMLInputFactoryResourcePool {

    /**
     * {@link XMLInputFactory} resource handler to manage
     * {@link XMLInputFactory} life cycle methods
     */
    private static class XMLInputFactoryResourceHandler implements ResourceHandler<XMLInputFactory> {

        public XMLInputFactoryResourceHandler() {
        }

        @Override
        public XMLInputFactory create() {
            return XMLInputFactory.newInstance();
        }

        @Override
        public void onRelease(XMLInputFactory xmlInputFactory) {
        }

        @Override
        public void onTake(XMLInputFactory xmlInputFactory) {
        }
    }
    
    private final GenericResourcePool<XMLInputFactory> xmlInputFactoryPool;

    /**
     * Allowing to instantiate a new {@link XMLInputFactoryResourcePool}
     * containing {@link XMLInputFactory} resources.
     * 
     * @param minPoolSize
     *            The minimum number of {@link XMLInputFactory} instances in the
     *            pool (created at the initialization).
     * @param maxPoolSize
     *            the maximum number of {@link XMLInputFactory} instances in the
     *            current pool (limit of the pool). It must be greater or equals
     *            to the specified minSize. The maximum value is
     *            Integer.MAX_VALUE
     * @param poolPolicy
     *            the {@link PoolPolicy} to adopt when the maximum size is
     *            reached. it cannot be null.
     */
    public XMLInputFactoryResourcePool(int minPoolSize, int maxPoolSize,
            PoolPolicy poolPolicy) {
        XMLInputFactoryResourceHandler xmlInputFactoryResourceHandler = new XMLInputFactoryResourceHandler();
        this.xmlInputFactoryPool = new GenericResourcePool<XMLInputFactory>(
                xmlInputFactoryResourceHandler, minPoolSize, maxPoolSize, poolPolicy);
    }

    /**
     * Take one unused {@link XMLInputFactory} in the current pool. After
     * getting a {@link XMLInputFactory} from the pool and before returning a
     * {@link XMLInputFactory}, the method onTake() of the
     * {@link XMLInputFactory} resource handler is called.
     * 
     * @return one {@link XMLInputFactory}
     * 
     * @throws PoolException
     *             if the current thread is interrupted for the pool policy WAIT
     *             or if there is no more available resource in the pool for the
     *             pool policy REJECT
     * 
     */
    public XMLInputFactory take() {
        return this.xmlInputFactoryPool.take();
    }

    /**
     * Release the specified {@link XMLInputFactory} After putting back the
     * {@link XMLInputFactory} in the pool, the method onRelease() of the
     * {@link XMLInputFactory} resource handler is called.
     * 
     * @param xmlInputFactory
     *            The {@link XMLInputFactory} to release
     */
    public final void release(final XMLInputFactory xmlInputFactory) {
        this.xmlInputFactoryPool.release(xmlInputFactory);
    }
}
