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

import org.w3c.dom.Document;

import com.ebmwebsourcing.easycommons.pooling.PoolException;
import com.ebmwebsourcing.easycommons.pooling.PoolPolicy;

/**
 * A static pool of JVM {@link DocumentBuilder} to improve JVM {@link DocumentBuilder} creation efficiency. 
 * <p>
 * A JVM {@link DocumentBuilder} is initialized the first time this class is used.
 * An infinite number (integer maximum value) of JVM {@link DocumentBuilder} may be created. They are never evicted.
 * </p>
 * 
 * @author Nicolas Oddoux - EBM WebSourcing
 */
public final class JVMDocumentBuilders {

    private static final JVMDocumentBuilderResourcePool jvmDocumentBuilderPool = new JVMDocumentBuilderResourcePool(1, Integer.MAX_VALUE, PoolPolicy.WAIT);

    /**
     * Take a JVM {@link DocumentBuilder} from the pool
     * 
     * @return one JVM {@link DocumentBuilder}
     * 
     * @throws PoolException
     *             if the current thread waits for a JVM {@link DocumentBuilder}
     *             of the pool and is interrupted
     */
    public final static DocumentBuilder takeJVMDocumentBuilder() throws PoolException {
       return jvmDocumentBuilderPool.take();
    }

    /**
     * Release a JVM {@link DocumentBuilder} to the pool
     * 
     * @param jvmDocumentBuilder a JVM {@link DocumentBuilder} to release
     */
    public final static void releaseJVMDocumentBuilder(DocumentBuilder jvmDocumentBuilder) {
        jvmDocumentBuilderPool.release(jvmDocumentBuilder);
    }
    
    /**
     * Create a new {@link Document} (with a JVM {@link DocumentBuilder} of the
     * pool)
     * 
     * @return a new {@link Document}
     * 
     * @throws PoolException
     *             if the current thread waits for a JVM {@link DocumentBuilder}
     *             of the pool and is interrupted
     */
    public final static Document newDocument() {
        DocumentBuilder jvmDocumentBuilder = takeJVMDocumentBuilder();
        Document document = jvmDocumentBuilder.newDocument();
        releaseJVMDocumentBuilder(jvmDocumentBuilder);
        
        return document;
    }
}
