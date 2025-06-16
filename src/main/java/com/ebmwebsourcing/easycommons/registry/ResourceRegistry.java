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
package com.ebmwebsourcing.easycommons.registry;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a generic implementation of a resource registry. All methods that
 * alter registry state are thread safe.
 * 
 * @author ofabre
 * 
 */
public class ResourceRegistry<T> {

    private Map<String, T> registry = new HashMap<String, T>();

    /**
     * 
     * @param key
     *            cannot be null
     * @param resource
     *            cannot be null
     * @throws KeyAlreadyBoundException
     *             if the provided key is already bound to a registered object
     */
    public synchronized void register(String key, T resource) {
        assert key != null;
        assert resource != null;

        T previouslyMappedRessource = registry.get(key);
        if (previouslyMappedRessource != null) {
            throw new KeyAlreadyBoundException("The provided key is already bound: " + key);
        } else {
            registry.put(key, resource);
        }
    }

    /**
     * 
     * @param key
     *            cannot be null
     * @throws KeyNotFoundException
     *             if the provided key isn't found in registry
     */
    public synchronized void unregister(String key) {
        assert key != null;

        T previouslyMappedRessource = registry.get(key);
        if (previouslyMappedRessource == null) {
            throw new KeyNotFoundException("The given key isn't found in registry: " + key);
        } else {
            registry.remove(key);
        }
    }

    /**
     * 
     * @param key
     *            cannot be null
     * @return the resource bound to this key or null if not found
     */
    public T lookup(String key) {
        assert key != null;

        return registry.get(key);
    }

}
