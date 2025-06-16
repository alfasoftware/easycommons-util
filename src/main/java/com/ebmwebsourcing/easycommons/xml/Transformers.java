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

import javax.xml.transform.Transformer;

import com.ebmwebsourcing.easycommons.pooling.PoolException;
import com.ebmwebsourcing.easycommons.pooling.PoolPolicy;

/**
 * A static pool of {@link Transformer} to improve {@link Transformer} creation efficiency. 
 * A {@link Transformer} is initialized the first time this class is used.
 * An infinite number (integer maximum value) of {@link Transformer} may be created. They are never evicted.
 * 
 * @author Nicolas Oddoux - EBM WebSourcing
 */
public final class Transformers {

    private static final TransformerResourcePool transformerPool = new TransformerResourcePool(1, Integer.MAX_VALUE, PoolPolicy.WAIT);

    /**
     * Take a {@link Transformer} from the pool
     * 
     * @return one {@link Transformer}
     * 
     * @throws PoolException
     *             if the current thread waits for a {@link Transformer}
     *             of the pool and is interrupted
     */
    public final static Transformer takeTransformer() throws PoolException {
       return transformerPool.take();
    }

    /**
     * Release a {@link Transformer} to the pool
     * 
     * @param transformer a {@link Transformer} to release
     */
    public final static void releaseTransformer(Transformer transformer) {
        transformerPool.release(transformer);
    }
}
