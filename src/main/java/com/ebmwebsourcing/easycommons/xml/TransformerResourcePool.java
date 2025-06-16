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

import java.util.Properties;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

import com.ebmwebsourcing.easycommons.lang.reflect.ReflectionHelper;
import com.ebmwebsourcing.easycommons.pooling.GenericResourcePool;
import com.ebmwebsourcing.easycommons.pooling.PoolException;
import com.ebmwebsourcing.easycommons.pooling.PoolPolicy;
import com.ebmwebsourcing.easycommons.pooling.ResourceHandler;

/**
 * This class represent a {@link Transformer} pool, which provided {@link Transformer}
 * object as resource.
 * 
 * @author aruffie
 * @author Nicolas Oddoux - EBM WebSourcing
 */
public class TransformerResourcePool {

    /**
     * {@link Transformer} resource handler to manage {@link Transformer} life cycle methods
     */
    private static class TransformerResourceHandler implements ResourceHandler<Transformer> {

        final TransformerFactory transformerFactory;

        public TransformerResourceHandler() {
            this.transformerFactory = TransformerFactory.newInstance();
        }

        @Override
        public Transformer create() {
            try {
                return this.transformerFactory.newTransformer();
            } catch (TransformerConfigurationException e) {
                throw new PoolException(e);
            }
        }

        @Override
        public void onRelease(Transformer transformer) {
            transformer.reset();
        }

        @Override
        public void onTake(Transformer transformer) {            
        }
    }
    
    private final GenericResourcePool<Transformer> transformerResourcePool;

    /**
     * Allowing to instantiate a new {@link TransformerResourcePool} containing {@link Transformer} resources.
     * 
     * @param minPoolSize
     *            The minimum number of {@link Transformer} instances in the pool (created at the
     *            initialization).
     * @param maxPoolSize
     *            the maximum number of {@link Transformer} instances in the current pool (limit of the
     *            pool). It must be greater or equals to the specified minSize.
     *            The maximum value is Integer.MAX_VALUE
     * @param poolPolicy
     *            the {@link PoolPolicy} to adopt when the maximum size is reached. it
     *            cannot be null.
     */
    public TransformerResourcePool(int minPoolSize, int maxPoolSize,
            PoolPolicy poolPolicy) {
        TransformerResourceHandler transformerResourceHandler = new TransformerResourceHandler();
        this.transformerResourcePool = new GenericResourcePool<Transformer>(
                transformerResourceHandler, minPoolSize, maxPoolSize, poolPolicy);
    }

    /**
     * Take one unused {@link Transformer} in the current pool. After getting a
     * {@link Transformer} from the pool and before returning a
     * {@link Transformer}, the method onTake() of the {@link Transformer}
     * resource handler is called.
     * 
     * @return one {@link Transformer}
     * 
     * @throws PoolException
     *             if the current thread is interrupted for the pool policy WAIT
     *             or if there is no more available resource in the pool for the
     *             pool policy REJECT
     * 
     */
    public Transformer take() {
        return this.transformerResourcePool.take();
    }

    /**
     * Release the specified {@link Transformer} After putting back the
     * {@link Transformer} in the pool, the method onRelease() of the
     * {@link Transformer} resource handler is called.
     * 
     * @param transformer
     *            The {@link Transformer} to release
     */
    public final void release(final Transformer transformer) {
        this.transformerResourcePool.release(transformer);
    }
}
