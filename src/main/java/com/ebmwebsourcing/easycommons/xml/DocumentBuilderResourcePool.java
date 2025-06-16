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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.ebmwebsourcing.easycommons.pooling.GenericResourcePool;
import com.ebmwebsourcing.easycommons.pooling.PoolException;
import com.ebmwebsourcing.easycommons.pooling.PoolPolicy;
import com.ebmwebsourcing.easycommons.pooling.ResourceHandler;

/**
 * This class represent a {@link DocumentBuilder} pool, which provided {@link DocumentBuilder}
 * object as resource.
 * 
 * @author Nicolas Oddoux - EBM WebSourcing
 */
public class DocumentBuilderResourcePool {

    /**
     * {@link DocumentBuilder} resource handler to manage {@link DocumentBuilder} life cycle methods
     */
    private static class DocumentBuilderResourceHandler implements ResourceHandler<DocumentBuilder> {

        final DocumentBuilderFactory documentBuilderFactory;

        public DocumentBuilderResourceHandler() {
            this.documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
        }

        @Override
        public DocumentBuilder create() {
            try {
                return this.documentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new PoolException(e);
            }
        }

        @Override
        public void onRelease(DocumentBuilder documentBuilder) {
            documentBuilder.reset();
        }

        @Override
        public void onTake(DocumentBuilder documentBuilder) {            
        }
    }
    
    private final GenericResourcePool<DocumentBuilder> documentBuilderResourcePool;

    /**
     * Allowing to instantiate a new {@link DocumentBuilderResourcePool} containing {@link DocumentBuilder} resources.
     * 
     * @param minPoolSize
     *            The minimum number of {@link DocumentBuilder} instances in the pool (created at the
     *            initialization).
     * @param maxPoolSize
     *            the maximum number of {@link DocumentBuilder} instances in the current pool (limit of the
     *            pool). It must be greater or equals to the specified minSize.
     *            The maximum value is Integer.MAX_VALUE
     * @param poolPolicy
     *            the {@link PoolPolicy} to adopt when the maximum size is reached. it
     *            cannot be null.
     */
    public DocumentBuilderResourcePool(int minPoolSize, int maxPoolSize,
            PoolPolicy poolPolicy) {
        DocumentBuilderResourceHandler documentBuilderResourceHandler = new DocumentBuilderResourceHandler();
        this.documentBuilderResourcePool = new GenericResourcePool<DocumentBuilder>(
                documentBuilderResourceHandler, minPoolSize, maxPoolSize, poolPolicy);
    }

    /**
     * Take one unused {@link DocumentBuilder} in the current pool. After
     * getting a {@link DocumentBuilder} from the pool and before returning
     * a {@link DocumentBuilder}, the method onTake() of the
     * {@link DocumentBuilder} resource handler is called.
     * 
     * @return one {@link DocumentBuilder}
     * 
     * @throws PoolException
     *             if the current thread is interrupted for the pool policy WAIT
     *             or if there is no more available resource in the pool for the
     *             pool policy REJECT
     * 
     */
    public DocumentBuilder take() {
        return this.documentBuilderResourcePool.take();
    }

    /**
     * Release the specified {@link DocumentBuilder} After putting back the
     * {@link DocumentBuilder} in the pool, the method onRelease() of the
     * resource {@link DocumentBuilder} resource handler is called.
     * 
     * @param documentBuilder
     *            The {@link DocumentBuilder} to release
     */
    public final void release(final DocumentBuilder documentBuilder) {
        this.documentBuilderResourcePool.release(documentBuilder);
    }
}
