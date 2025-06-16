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
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;

import com.ebmwebsourcing.easycommons.uri.URIHelper;


public class DefaultURIResolver implements URIResolver {


    @Override
    public Source resolve(String hRef, String baseURI) throws TransformerException {
        try {
            URI base = null;
            if(baseURI != null) {
                base = URI.create(baseURI);
            }
            URI uri = URIHelper.resolve(base, hRef);
            if(uri != null && uri.isAbsolute()) {
                return new SAXSource(new InputSource(uri.toURL().openStream()));
            }
        } catch (MalformedURLException e) {
            throw new TransformerException(e);
        } catch (IOException e) {
            throw new TransformerException(e);
        } catch (URISyntaxException e) {
            throw new TransformerException(e);
        } 
        return null;
    }



}
