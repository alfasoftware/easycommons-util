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

import javax.xml.stream.XMLOutputFactory;

import com.ebmwebsourcing.easycommons.pooling.GenericResourcePool;
import com.ebmwebsourcing.easycommons.pooling.PoolException;
import com.ebmwebsourcing.easycommons.pooling.PoolPolicy;
import com.ebmwebsourcing.easycommons.pooling.ResourceHandler;

/**
 * This class represent a {@link XMLOutputFactory} pool, which provided {@link XMLOutputFactory}
 * object as resource.
 * 
 * @author Nicolas Oddoux - EBM WebSourcing
 */
public class XMLOutputFactoryResourcePool {

    /**
     * {@link XMLOutputFactory} resource handler to manage {@link XMLOutputFactory} life cycle methods
     */
    private static class XMLOutputFactoryResourceHandler implements ResourceHandler<XMLOutputFactory> {

        public XMLOutputFactoryResourceHandler() {
        }

        @Override
        public XMLOutputFactory create() {
            return XMLOutputFactory.newInstance();
        }

        @Override
        public void onRelease(XMLOutputFactory xmlOutputFactory) {
        }

        @Override
        public void onTake(XMLOutputFactory xmlOutputFactory) {            
        }
    }
    
    private final GenericResourcePool<XMLOutputFactory> xmlOutputFactoryPool;

    /**
     * Allowing to instantiate a new {@link XMLOutputFactoryResourcePool} containing {@link XMLOutputFactory} resources.
     * 
     * @param minPoolSize
     *            The minimum number of {@link XMLOutputFactory} instances in the pool (created at the
     *            initialization).
     * @param maxPoolSize
     *            the maximum number of {@link XMLOutputFactory} instances in the current pool (limit of the
     *            pool). It must be greater or equals to the specified minSize.
     *            The maximum value is Integer.MAX_VALUE
     * @param poolPolicy
     *            the {@link PoolPolicy} to adopt when the maximum size is reached. it
     *            cannot be null.
     */
    public XMLOutputFactoryResourcePool(int minPoolSize, int maxPoolSize,
            PoolPolicy poolPolicy) {
        XMLOutputFactoryResourceHandler xmlOutputFactoryResourceHandler = new XMLOutputFactoryResourceHandler();
        this.xmlOutputFactoryPool = new GenericResourcePool<XMLOutputFactory>(
                xmlOutputFactoryResourceHandler, minPoolSize, maxPoolSize, poolPolicy);
    }

    /**
     * Take one unused {@link XMLOutputFactory} in the current pool. After getting a
     * {@link XMLOutputFactory} from the pool and before returning a
     * {@link XMLOutputFactory}, the method onTake() of the {@link XMLOutputFactory}
     * resource handler is called.
     * 
     * @return one {@link XMLOutputFactory}
     * 
     * @throws PoolException
     *             if the current thread is interrupted for the pool policy WAIT
     *             or if there is no more available resource in the pool for the
     *             pool policy REJECT
     * 
     */
    public XMLOutputFactory take() {
        return this.xmlOutputFactoryPool.take();
    }

    /**
     * Release the specified {@link XMLOutputFactory} After putting back the
     * {@link XMLOutputFactory} in the pool, the method onRelease() of the
     * {@link XMLOutputFactory} resource handler is called.
     * 
     * @param xmlOutputFactory
     *            The {@link XMLOutputFactory} to release
     */
    public final void release(final XMLOutputFactory xmlOutputFactory) {
        this.xmlOutputFactoryPool.release(xmlOutputFactory);
    }
}
