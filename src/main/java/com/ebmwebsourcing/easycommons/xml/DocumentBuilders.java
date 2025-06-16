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
 * A static pool of {@link DocumentBuilder} to improve {@link DocumentBuilder} creation efficiency. 
 * A {@link DocumentBuilder} is initialized the first time this class is used.
 * An infinite number (integer maximum value) of {@link DocumentBuilder} may be created. They are never evicted.
 * 
 * @author Nicolas Oddoux - EBM WebSourcing
 */
public final class DocumentBuilders {

    private static final DocumentBuilderResourcePool documentBuilderPool = new DocumentBuilderResourcePool(1, Integer.MAX_VALUE, PoolPolicy.WAIT);

    /**
     * Take a {@link DocumentBuilder} from the pool
     * 
     * @return one {@link DocumentBuilder}
     * 
     * @throws PoolException
     *             if the current thread waits for a {@link DocumentBuilder}
     *             of the pool and is interrupted
     */
    public final static DocumentBuilder takeDocumentBuilder() throws PoolException {
       return documentBuilderPool.take();
    }

    /**
     * Release a {@link DocumentBuilder} to the pool
     * 
     * @param documentBuilder a {@link DocumentBuilder} to release
     */
    public final static void releaseDocumentBuilder(DocumentBuilder documentBuilder) {
        documentBuilderPool.release(documentBuilder);
    }

    /**
     * Create a new {@link Document} (with a {@link DocumentBuilder} of the
     * pool)
     * 
     * @return a new {@link Document}
     * 
     * @throws PoolException
     *             if the current thread waits for a {@link DocumentBuilder} of
     *             the pool and is interrupted when the {@link PoolPolicy} is
     *             WAIT or if there is no more available {@link DocumentBuilder}
     *             in the {@link DocumentBuilder} pool when the
     *             {@link PoolPolicy} is REJECT
     */
    public final static Document newDocument() {
        final DocumentBuilder documentBuilder = takeDocumentBuilder();
        try {
            return documentBuilder.newDocument();
        } finally{
            releaseDocumentBuilder(documentBuilder);
        }
    }
}
