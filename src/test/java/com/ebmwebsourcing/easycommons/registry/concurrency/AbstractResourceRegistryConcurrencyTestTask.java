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
package com.ebmwebsourcing.easycommons.registry.concurrency;

import java.util.concurrent.atomic.AtomicInteger;

import com.ebmwebsourcing.easycommons.registry.ResourceRegistry;

/**
 * @author olivierfabre
 *
 */
public abstract class AbstractResourceRegistryConcurrencyTestTask implements Runnable {

    protected AtomicInteger counter;

    protected ResourceRegistry<Object> resourceRegistry;
    
    protected final String keyToUseForRegisteredObject;

    public AbstractResourceRegistryConcurrencyTestTask(AtomicInteger counter, ResourceRegistry<Object> resourceRegistry, String keyToUseForRegisteredObject) {
        super();
        this.counter = counter;
        this.resourceRegistry = resourceRegistry;
        this.keyToUseForRegisteredObject = keyToUseForRegisteredObject;
    }

}
