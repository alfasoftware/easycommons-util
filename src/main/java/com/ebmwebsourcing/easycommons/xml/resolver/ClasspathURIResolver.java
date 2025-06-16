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
package com.ebmwebsourcing.easycommons.xml.resolver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;


public class ClasspathURIResolver implements URIResolver {


    @Override
    public Source resolve(String hRef, String baseURI) throws TransformerException {
        try {
            if(hRef != null) {
                URL url = Thread.currentThread().getContextClassLoader().getResource(hRef);
                if(url != null) {
                    return new SAXSource(new InputSource(url.openStream()));
                }
            }
            if(baseURI != null) {
                URL url = Thread.currentThread().getContextClassLoader().getResource(baseURI);
                if(url != null) {
                    return new SAXSource(new InputSource(url.openStream()));
                }
            }
        } catch (MalformedURLException e) {
            throw new TransformerException(e);
        } catch (IOException e) {
            throw new TransformerException(e);
        } 

        return null;
    }



}
