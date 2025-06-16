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

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.ebmwebsourcing.easycommons.pooling.PoolException;
import com.ebmwebsourcing.easycommons.pooling.PoolPolicy;

/**
 * A static pool of {@link XMLInputFactory} to improve {@link XMLInputFactory}
 * creation efficiency. A {@link XMLInputFactory} is initialized the first time
 * this class is used. An infinite number (integer maximum value) of
 * {@link XMLInputFactory} may be created. They are never evicted.
 * 
 * @author Christophe DENEUX - EBM WebSourcing
 */
public final class XMLInputFactories {

    private static final XMLInputFactoryResourcePool xmlInputFactoryPool = new XMLInputFactoryResourcePool(
            1, Integer.MAX_VALUE, PoolPolicy.WAIT);

    /**
     * Take a {@link XMLInputFactory} from the pool
     * 
     * @return one {@link XMLInputFactory}
     * 
     * @throws PoolException
     *             if the current thread waits for a {@link XMLInputFactory} of
     *             the pool and is interrupted
     */
    public final static XMLInputFactory takeXMLInputFactory() throws PoolException {
        return xmlInputFactoryPool.take();
    }

    /**
     * Release a {@link XMLInputFactory} to the pool
     * 
     * @param xmlInputFactory
     *            a {@link XMLInputFactory} to release
     */
    public final static void releaseXMLInputFactory(final XMLInputFactory xmlInputFactory) {
        xmlInputFactoryPool.release(xmlInputFactory);
    }

    /**
     * Create a {@link XMLStreamReader} from a specified {@link InputStream} by
     * using the pool of {@link XMLInputFactory}
     * 
     * @param InputStream
     *            an {@link InputStream}
     * 
     * @return the {@link XMLStreamReader}
     * 
     * @throws XMLStreamException
     *             if an unexpected processing errors occurs
     */
    public final static XMLStreamReader createXMLStreamReader(final InputStream InputStream)
            throws XMLStreamException {
        XMLInputFactory xmlInputFactory = null;

        try {
            xmlInputFactory = takeXMLInputFactory();
            return xmlInputFactory.createXMLStreamReader(InputStream);
        } finally {
            if(xmlInputFactory != null) {
                releaseXMLInputFactory(xmlInputFactory);
            }
        }
    }
}
