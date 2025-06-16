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
package com.ebmwebsourcing.easycommons.pooling;

/**
 * A resource handler to manage the resource life cycle. 
 * It enables to control the creation of the resource and to make specific 
 * actions on the getting a resource from the pool and on the putting of
 * a resource to the pool
 * 
 * @author aruffie
 * @author Nicolas Oddoux - EBM WebSourcing
 */
public interface ResourceHandler<T> {

    /**
     * Create a resource.
     * 
     * @return a new resource instance.
     */
    T create();

    /**
     * Call when getting a resource from the pool (taking)
     * 
     * @param resource
     *            the resource got from the pool
     */
    void onTake(final T resource);

    /**
     * Call when putting a resource back in the pool (releasing)
     * 
     * @param resource
     *            The resource put back in the pool
     */
    void onRelease(final T resource);
}
