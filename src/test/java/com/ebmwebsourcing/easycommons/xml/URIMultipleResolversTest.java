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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.junit.Assert;
import org.junit.Test;

import com.ebmwebsourcing.easycommons.xml.resolver.URIMultipleResolvers;

public class URIMultipleResolversTest {

    @Test
    public void testAllNull() throws TransformerException {
    	Source s = new URIMultipleResolvers().resolve(null, null);
        Assert.assertNull(s);
    }

    @Test
    public void testBaseUriNull() throws TransformerException {
        Assert.assertNotNull(new URIMultipleResolvers().resolve(null, "test.xml"));
    }

    @Test
    public void testFile() throws TransformerException {
        File f = new File("./src/test/resources");
        Assert.assertNotNull(new URIMultipleResolvers().resolve("test.xml", f.toURI().toString()));
    }

    @Test
    public void testJar() throws TransformerException, URISyntaxException {
        URI jar = new URI("jar:" + new File("./src/test/resources/ws-echo.jar").toURI() + "!/wsdl/echo.wsdl");
        Assert.assertEquals("jar", jar.getScheme());
        Assert.assertNotNull(new URIMultipleResolvers().resolve(jar.toString(), null));
    }


    @Test
    public void testSpace() throws URISyntaxException, TransformerException {
        File f = new File("./src/test/resources/petals link/");
        Assert.assertNotNull(new URIMultipleResolvers().resolve("test.xml", f.toURI().toString()));
    }
    

}
