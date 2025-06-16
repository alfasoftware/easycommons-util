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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

/**
 * @author mjambert
 *
 */
/**
 * @author mjambert
 *
 */
/**
 * @author mjambert
 *
 */
public class DefaultNamespaceContext implements NamespaceContext {

    private final Map<String, String> namespaces; 
    
    public DefaultNamespaceContext() {
        this.namespaces = new HashMap<String, String>();
    }
    
    
    public void bindNamespace(String prefix, String namespaceURI) {
        namespaces.put(prefix, namespaceURI);
    }
    
    
    @Override
    public String getNamespaceURI(String prefix) {
        assert prefix != null;
        return namespaces.get(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        assert namespaceURI != null;
        for (Map.Entry<String, String> entry : namespaces.entrySet()) {
            if (namespaceURI.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        return Collections.unmodifiableMap(namespaces).keySet().iterator();
    }
    
    /**
     * Get bounded namespaces known to this {@link DefaultNamespaceContext}.
     * 
     * @return A read-only map containing all bounded namespaces. 
     */
    public Map<String, String> getNamespaces() {
        return Collections.unmodifiableMap(namespaces);
    }
    
    

}
