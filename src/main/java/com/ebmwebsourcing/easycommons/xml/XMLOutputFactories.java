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

import java.io.OutputStream;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.ebmwebsourcing.easycommons.pooling.PoolException;
import com.ebmwebsourcing.easycommons.pooling.PoolPolicy;

/**
 * A static pool of {@link XMLOutputFactory} to improve {@link XMLOutputFactory} creation efficiency. 
 * A {@link XMLOutputFactory} is initialized the first time this class is used.
 * An infinite number (integer maximum value) of {@link XMLOutputFactory} may be created. They are never evicted.
 * 
 * @author Nicolas Oddoux - EBM WebSourcing
 */
public final class XMLOutputFactories {

    private static final XMLOutputFactoryResourcePool xmlOutputFactoryPool = new XMLOutputFactoryResourcePool(1, Integer.MAX_VALUE, PoolPolicy.WAIT);

    /**
     * Take a {@link XMLOutputFactory} from the pool
     * 
     * @return one {@link XMLOutputFactory}
     * 
     * @throws PoolException
     *             if the current thread waits for a {@link XMLOutputFactory}
     *             of the pool and is interrupted
     */
    public final static XMLOutputFactory takeXMLOutputFactory() throws PoolException {
       return xmlOutputFactoryPool.take();
    }

    /**
     * Release a {@link XMLOutputFactory} to the pool
     * 
     * @param xmlOutputFactory a {@link XMLOutputFactory} to release
     */
    public final static void releaseXMLOutputFactory(XMLOutputFactory xmlOutputFactory) {
        xmlOutputFactoryPool.release(xmlOutputFactory);
    }

    /**
     * Create a {@link XMLStreamWriter} from a specified {@link OutputStream} by
     * using the pool of {@link XMLOutputFactory}
     * 
     * @param outputStream
     *            an {@link OutputStream}
     * 
     * @return the {@link XMLStreamWriter}
     * 
     * @throws XMLStreamException
     *             if an unexpected processing errors occurs
     */
    public final static XMLStreamWriter createXMLStreamWriter(OutputStream outputStream)
            throws XMLStreamException {
        XMLOutputFactory xmlOutputFactory = null;

        try {
            xmlOutputFactory = takeXMLOutputFactory();
            return xmlOutputFactory.createXMLStreamWriter(outputStream);
        } finally {
            if (xmlOutputFactory != null) {
                releaseXMLOutputFactory(xmlOutputFactory);
            }
        }
    }
}
