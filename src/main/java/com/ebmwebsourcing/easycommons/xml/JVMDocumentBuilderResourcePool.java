/**
 * Copyright (c) 2011-2012 EBM WebSourcing, 2012-2023 Linagora
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
 * This class represent a JVM {@link DocumentBuilder} pool, which provided JVM {@link DocumentBuilder}
 * object as resource.
 * 
 * @author Nicolas Oddoux - EBM WebSourcing
 */
public class JVMDocumentBuilderResourcePool {

    /**
     * JVM {@link DocumentBuilder} resource handler to manage JVM {@link DocumentBuilder} life cycle methods
     */
    private static class JVMDocumentBuilderResourceHandler implements ResourceHandler<DocumentBuilder> {

        final DocumentBuilderFactory jvmDocumentBuilderFactory;

        public JVMDocumentBuilderResourceHandler() {
            ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
                Thread.currentThread().setContextClassLoader(systemClassLoader);

                this.jvmDocumentBuilderFactory = DocumentBuilderFactory
                        .newInstance();
            } finally {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
            }
            
            this.jvmDocumentBuilderFactory.setNamespaceAware(true);
        }

        @Override
        public DocumentBuilder create() {
            try {
                return this.jvmDocumentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new PoolException(e);
            }
        }

        @Override
        public void onRelease(DocumentBuilder jvmDocumentBuilder) {
        }

        @Override
        public void onTake(DocumentBuilder jvmDocumentBuilder) {            
        }
    }
    
    private final GenericResourcePool<DocumentBuilder> jvmDocumentBuilderResourcePool;

    /**
     * Allowing to instantiate a new {@link JVMDocumentBuilderResourcePool} containing JVM {@link DocumentBuilder} resources.
     * 
     * @param minPoolSize
     *            The minimum number of JVM {@link DocumentBuilder} instances in the pool (created at the
     *            initialization).
     * @param maxPoolSize
     *            the maximum number of JVM {@link DocumentBuilder} instances in the current pool (limit of the
     *            pool). It must be greater or equals to the specified minSize.
     *            The maximum value is Integer.MAX_VALUE
     * @param poolPolicy
     *            the {@link PoolPolicy} to adopt when the maximum size is reached. it
     *            cannot be null.
     */
    public JVMDocumentBuilderResourcePool(int minPoolSize, int maxPoolSize,
            PoolPolicy poolPolicy) {
        JVMDocumentBuilderResourceHandler jvmDocumentBuilderResourceHandler = new JVMDocumentBuilderResourceHandler();
        this.jvmDocumentBuilderResourcePool = new GenericResourcePool<DocumentBuilder>(
                jvmDocumentBuilderResourceHandler, minPoolSize, maxPoolSize, poolPolicy);
    }

    /**
     * Take one unused JVM {@link DocumentBuilder} in the current pool. After getting a
     * JVM {@link DocumentBuilder} from the pool and before returning a
     * JVM {@link DocumentBuilder}, the method onTake() of the JVM {@link DocumentBuilder}
     * resource handler is called.
     * 
     * @return one JVM {@link DocumentBuilder}
     * 
     * @throws PoolException
     *             if the current thread is interrupted for the pool policy WAIT
     *             or if there is no more available resource in the pool for the
     *             pool policy REJECT
     * 
     */
    public DocumentBuilder take() {
        return this.jvmDocumentBuilderResourcePool.take();
    }

    /**
     * Release the specified JVM {@link DocumentBuilder} After putting back the
     * JVM {@link DocumentBuilder} in the pool, the method onRelease() of the
     * JVM {@link DocumentBuilder} resource handler is called.
     * 
     * @param jvmDocumentBuilder
     *            The JVM {@link DocumentBuilder} to release
     */
    public final void release(final DocumentBuilder jvmDocumentBuilder) {
        this.jvmDocumentBuilderResourcePool.release(jvmDocumentBuilder);
    }
}
