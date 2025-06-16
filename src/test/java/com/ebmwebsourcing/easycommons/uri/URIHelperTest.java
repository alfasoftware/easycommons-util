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
package com.ebmwebsourcing.easycommons.uri;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

public class URIHelperTest {

    @Test
    public void testAllNull() throws MalformedURLException, URISyntaxException {
        Assert.assertNull(URIHelper.resolve(null, null));
    }

    @Test
    public void testBaseUriNull() throws MalformedURLException, URISyntaxException {
        Assert.assertEquals(new URI("crisis.xsd"), URIHelper.resolve(null, "crisis.xsd"));
    }

    @Test
    public void testFile() throws MalformedURLException, URISyntaxException {
        File f = new File("./petalslink/testParent.xml");
        File expectedChildFile = new File(f.getParent(), "testChild.xml");

        Assert.assertEquals(expectedChildFile.toURI().normalize(),
                URIHelper.resolve(f.toURI(), "testChild.xml"));
    }

    @Test
    public void testJar() throws MalformedURLException, URISyntaxException {
        URI jar = new URI("jar:file:/D:/Projects/PetalsLink/test.jar!/fireman.wsdl");
        Assert.assertEquals("jar", jar.getScheme());
        Assert.assertEquals(new URI("jar:file:/D:/Projects/PetalsLink/test.jar!/crisis.xsd"),
                URIHelper.resolve(jar, "crisis.xsd"));
    }

    @Test
    public void testHTTP() throws MalformedURLException, URISyntaxException {
        URI http = new URI("http://www.petalsink.org/fireman.wsdl");
        Assert.assertEquals("http", http.getScheme());
        Assert.assertEquals(new URI("http://www.petalsink.org/crisis.xsd"),
                URIHelper.resolve(http, "crisis.xsd"));
    }

    @Test
    public void testSpace() throws MalformedURLException, URISyntaxException {
        File f = new File("./petals link/testParent.xml");
        File expectedChildFile = new File(f.getParent(), "testChild.xml");

        Assert.assertEquals(expectedChildFile.toURI().normalize(),
                URIHelper.resolve(f.toURI(), "testChild.xml"));
    }
}
